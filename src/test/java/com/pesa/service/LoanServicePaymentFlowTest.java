package com.pesa.service;

import com.pesa.config.LoanPolicyProperties;
import com.pesa.entity.CreditBoardScoreLite;
import com.pesa.entity.KycProfileLite;
import com.pesa.entity.Loan;
import com.pesa.entity.LoanPayment;
import com.pesa.entity.UserAccount;
import com.pesa.repository.CreditBoardScoreLiteRepository;
import com.pesa.repository.KycProfileLiteRepository;
import com.pesa.repository.LoanPaymentRepository;
import com.pesa.repository.LoanRepository;
import com.pesa.repository.UserAccountRepository;
import com.pesa.service.credit.CreditProviderClient;
import com.pesa.service.payment.PaymentGatewayClient;
import com.pesa.util.LoanCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanServicePaymentFlowTest {

    private LoanRepository loanRepository;
    private LoanPaymentRepository loanPaymentRepository;
    private UserAccountRepository userAccountRepository;
    private KycProfileLiteRepository kycProfileLiteRepository;
    private CreditBoardScoreLiteRepository creditBoardScoreLiteRepository;
    private CreditProviderClient creditProviderClient;
    private PaymentGatewayClient paymentGatewayClient;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        loanPaymentRepository = mock(LoanPaymentRepository.class);
        userAccountRepository = mock(UserAccountRepository.class);
        kycProfileLiteRepository = mock(KycProfileLiteRepository.class);
        creditBoardScoreLiteRepository = mock(CreditBoardScoreLiteRepository.class);
        creditProviderClient = mock(CreditProviderClient.class);
        paymentGatewayClient = mock(PaymentGatewayClient.class);

        LoanPolicyProperties policy = new LoanPolicyProperties();
        LoanCalculator calculator = new LoanCalculator(policy);

        loanService = new LoanService(
                loanRepository,
                loanPaymentRepository,
                userAccountRepository,
                kycProfileLiteRepository,
                creditBoardScoreLiteRepository,
                creditProviderClient,
                paymentGatewayClient,
                calculator,
                policy
        );
    }

    @Test
    void shouldRejectAcceptLoanWhenPinIsInvalid() {
        Loan loan = sampleApprovedLoan();
        UserAccount user = sampleActiveUser("1234");

        when(loanRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(loan));
        when(userAccountRepository.findById(10L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> loanService.acceptLoan(10L, 1L, "9999", "Bearer tkn"));

        assertEquals("Invalid PIN", ex.getMessage());
        verify(paymentGatewayClient, never()).requestDisbursement(any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldSkipDuplicateRepaymentEventByTransactionId() {
        when(loanPaymentRepository.findByTransactionId(200L)).thenReturn(Optional.of(new LoanPayment()));

        assertDoesNotThrow(() -> loanService.recordPaymentFromEvent(1L, 200L, new BigDecimal("1000")));

        verify(loanPaymentRepository, never()).save(any(LoanPayment.class));
    }

    @Test
    void shouldReopenClosedLoanWhenReversalDropsPaidAmountBelowDue() {
        Loan closedLoan = sampleClosedLoan();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(closedLoan));
        when(loanPaymentRepository.deleteByTransactionId(300L)).thenReturn(1L);
        when(loanPaymentRepository.sumAmountsByLoanId(1L)).thenReturn(new BigDecimal("500"));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        loanService.reversePaymentFromEvent(1L, 300L);

        assertEquals(Loan.LoanStatus.ACTIVE, closedLoan.getStatus());
        verify(loanRepository).save(closedLoan);
    }

    @Test
    void shouldFailOnOutOfOrderDisbursementEvent() {
        Loan pendingLoan = Loan.builder()
                .id(99L)
                .userId(10L)
                .principalAmount(new BigDecimal("100000"))
                .applicationFee(new BigDecimal("5000"))
                .monthlyInterest(new BigDecimal("3500"))
                .totalAmountDue(new BigDecimal("108500"))
                .durationMonths(1)
                .dueDate(LocalDate.now().plusMonths(1))
                .status(Loan.LoanStatus.PENDING)
                .build();

        when(loanRepository.findById(99L)).thenReturn(Optional.of(pendingLoan));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> loanService.markDisbursementPosted(99L));
        assertEquals("Invalid loan transition: PENDING -> ACTIVE", ex.getMessage());
    }

    private Loan sampleApprovedLoan() {
        return Loan.builder()
                .id(1L)
                .userId(10L)
                .principalAmount(new BigDecimal("100000"))
                .applicationFee(new BigDecimal("5000"))
                .monthlyInterest(new BigDecimal("3500"))
                .totalAmountDue(new BigDecimal("108500"))
                .durationMonths(1)
                .dueDate(LocalDate.now().plusMonths(1))
                .status(Loan.LoanStatus.APPROVED)
                .build();
    }

    private Loan sampleClosedLoan() {
        return Loan.builder()
                .id(1L)
                .userId(10L)
                .principalAmount(new BigDecimal("100000"))
                .applicationFee(new BigDecimal("5000"))
                .monthlyInterest(new BigDecimal("3500"))
                .totalAmountDue(new BigDecimal("108500"))
                .durationMonths(1)
                .dueDate(LocalDate.now().plusMonths(1))
                .status(Loan.LoanStatus.CLOSED)
                .build();
    }

    private UserAccount sampleActiveUser(String pin) {
        UserAccount user = mock(UserAccount.class);
        when(user.getId()).thenReturn(10L);
        when(user.getStatus()).thenReturn(UserAccount.UserStatus.ACTIVE);
        when(user.getPin()).thenReturn(pin);
        return user;
    }
}
