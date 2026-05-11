package com.pesa.service;

import com.pesa.entity.Loan;
import com.pesa.entity.LoanPayment;
import com.pesa.entity.CreditBoardScoreLite;
import com.pesa.entity.KycProfileLite;
import com.pesa.entity.UserAccount;
import com.pesa.dto.credit.CreditProfileResponse;
import com.pesa.repository.CreditBoardScoreLiteRepository;
import com.pesa.repository.KycProfileLiteRepository;
import com.pesa.repository.LoanRepository;
import com.pesa.repository.LoanPaymentRepository;
import com.pesa.repository.UserAccountRepository;
import com.pesa.service.credit.CreditProviderClient;
import com.pesa.util.LoanCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final UserAccountRepository userAccountRepository;
    private final KycProfileLiteRepository kycProfileLiteRepository;
    private final CreditBoardScoreLiteRepository creditBoardScoreLiteRepository;
    private final CreditProviderClient creditProviderClient;
    private final LoanCalculator loanCalculator;

    public LoanCalculator.LoanBreakdown calculateLoan(BigDecimal amount, Integer durationMonths) {
        return loanCalculator.calculate(amount, durationMonths);
    }

    public List<Map<String, Object>> getEligibleProducts(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        KycProfileLite kyc = kycProfileLiteRepository.findByUserId(userId).orElse(null);
        CreditBoardScoreLite credit = creditBoardScoreLiteRepository.findByUserId(userId).orElse(null);
        CreditProfileResponse externalCredit = creditProviderClient.fetchProfile(userId).orElse(null);

        boolean userActive = user.getStatus() == UserAccount.UserStatus.ACTIVE;
        boolean kycApproved = kyc != null && kyc.getStatus() == KycProfileLite.KycStatus.APPROVED;
        boolean creditEligible = credit != null && Boolean.TRUE.equals(credit.getEligible());
        BigDecimal creditLimit = credit != null && credit.getLoanLimit() != null
            ? credit.getLoanLimit() : new BigDecimal("100000");
        BigDecimal creditScore = credit != null && credit.getScore() != null
            ? credit.getScore() : new BigDecimal("500");
        BigDecimal utilizationRatio = BigDecimal.ZERO;
        Integer numberOfDefaults = 0;
        String recommendation = "Approve";

        if (externalCredit != null) {
            if (externalCredit.creditWorthiness() != null && externalCredit.creditWorthiness().score() != null) {
                if (externalCredit.creditWorthiness().score().value() != null) {
                    creditScore = externalCredit.creditWorthiness().score().value();
                }
                if (externalCredit.creditWorthiness().score().riskPercentage() != null) {
                    BigDecimal riskPct = externalCredit.creditWorthiness().score().riskPercentage();
                    if (riskPct.compareTo(new BigDecimal("35")) > 0) {
                        creditEligible = false;
                    }
                }
            }
            if (externalCredit.creditWorthiness() != null && externalCredit.creditWorthiness().recommendation() != null) {
                recommendation = externalCredit.creditWorthiness().recommendation();
                if ("Decline".equalsIgnoreCase(recommendation)) {
                    creditEligible = false;
                }
            }
            if (externalCredit.payment() != null && externalCredit.payment().debtBalance() != null
                    && externalCredit.payment().debtBalance().utilizationRatio() != null) {
                utilizationRatio = externalCredit.payment().debtBalance().utilizationRatio();
            }
            if (externalCredit.payment() != null && externalCredit.payment().paymentRemark() != null
                    && externalCredit.payment().paymentRemark().numberOfDefaults() != null) {
                numberOfDefaults = externalCredit.payment().paymentRemark().numberOfDefaults();
                if (numberOfDefaults > 0) {
                    creditEligible = false;
                }
            }
        }

        long completedLoans = loanRepository.findByUserId(userId, org.springframework.data.domain.PageRequest.of(0, 200))
            .stream()
            .filter(l -> l.getStatus() == Loan.LoanStatus.CLOSED || l.getStatus() == Loan.LoanStatus.COMPLETED)
            .count();

        BigDecimal stepUpMultiplier = completedLoans >= 3 ? new BigDecimal("1.50")
            : completedLoans >= 1 ? new BigDecimal("1.25")
            : BigDecimal.ONE;

        BigDecimal utilizationFactor = utilizationRatio.compareTo(new BigDecimal("0.8")) > 0 ? new BigDecimal("0.5")
                : utilizationRatio.compareTo(new BigDecimal("0.6")) > 0 ? new BigDecimal("0.75")
                : BigDecimal.ONE;
        BigDecimal dynamicCap = creditLimit.multiply(stepUpMultiplier).multiply(utilizationFactor);
        String riskBand = creditScore.compareTo(new BigDecimal("700")) >= 0 ? "LOW"
            : creditScore.compareTo(new BigDecimal("600")) >= 0 ? "MEDIUM"
            : "HIGH";

        List<Map<String, Object>> templates = List.of(
            template("instant", "INSTANT", "Mkopo wa Haraka", 50000, 500000, 7, 30),
            template("emergency", "EMERGENCY", "Mkopo wa Dharura", 100000, 1500000, 14, 90),
            template("business", "BUSINESS", "Mkopo wa Biashara", 300000, 5000000, 30, 360)
        );

        final boolean finalCreditEligible = creditEligible;
        final BigDecimal finalCreditScore = creditScore;
        final BigDecimal finalUtilizationRatio = utilizationRatio;
        final Integer finalNumberOfDefaults = numberOfDefaults;
        final String finalRecommendation = recommendation;

        List<Map<String, Object>> results = new java.util.ArrayList<>();
        for (Map<String, Object> t : templates) {
            BigDecimal baseMax = new BigDecimal(String.valueOf(t.get("maxAmount")));
            BigDecimal finalMax = baseMax.min(dynamicCap).max(new BigDecimal(String.valueOf(t.get("minAmount"))));
            boolean eligible = userActive && kycApproved && finalCreditEligible
                && finalMax.compareTo(new BigDecimal(String.valueOf(t.get("minAmount")))) >= 0;

            String reason = null;
            if (!userActive) {
                reason = "Account blocked";
            } else if (!kycApproved) {
                reason = "Complete KYC first";
            } else if (!finalCreditEligible) {
                reason = "Credit profile not eligible";
            } else if (finalNumberOfDefaults != null && finalNumberOfDefaults > 0) {
                reason = "Credit defaults detected";
            }

            Map<String, Object> out = new LinkedHashMap<>(t);
            out.put("monthlyInterestRate", 0.035);
            out.put("applicationFeePct", 5.0);
            out.put("penaltyMonthlyPct", 5.0);
            out.put("maxAmount", finalMax.setScale(0, java.math.RoundingMode.DOWN));
            out.put("eligible", eligible);
            out.put("reasonIfNotEligible", reason);
            out.put("recommendedAmount", finalMax.multiply(new BigDecimal("0.6")).setScale(0, java.math.RoundingMode.DOWN));
            out.put("riskBand", riskBand);
            out.put("creditScore", finalCreditScore);
            out.put("utilizationRatio", finalUtilizationRatio);
            out.put("numberOfDefaults", finalNumberOfDefaults);
            out.put("creditRecommendation", finalRecommendation);
            results.add(out);
        }
        return results;
    }

    @Transactional
    public Loan applyForLoan(Long userId, BigDecimal principalAmount, Integer durationMonths) {
        validateEligibilityForApplication(userId);

        LoanCalculator.LoanBreakdown breakdown = calculateLoan(principalAmount, durationMonths);

        Loan loan = Loan.builder()
            .userId(userId)
            .principalAmount(principalAmount)
            .applicationFee(breakdown.getApplicationFee())
            .monthlyInterest(breakdown.getTotalInterest())
            .totalAmountDue(breakdown.getTotalAmountDue())
            .durationMonths(durationMonths)
            .status(Loan.LoanStatus.PENDING)
            .dueDate(LocalDate.now().plusMonths(durationMonths))
            .build();

        loan = loanRepository.save(loan);
        log.info("Loan application created: loanId={}, userId={}, amount={}", loan.getId(), userId, principalAmount);

        return loan;
    }

    @Transactional
    public Loan approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus(Loan.LoanStatus.APPROVED);
        loan.setApprovedAt(java.time.LocalDateTime.now());
        loan = loanRepository.save(loan);

        log.info("Loan approved: {}", loanId);
        return loan;
    }

    @Transactional
    public Loan disburseLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != Loan.LoanStatus.APPROVED) {
            throw new RuntimeException("Loan must be approved before disbursement");
        }

        loan.setStatus(Loan.LoanStatus.DISBURSED);
        loan.setDisbursedAt(java.time.LocalDateTime.now());
        loan = loanRepository.save(loan);

        log.info("Loan disbursed: {}", loanId);
        return loan;
    }

    public Loan getLoan(Long loanId) {
        return loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    public Loan getLoanForUser(Long loanId, Long userId) {
        return loanRepository.findByIdAndUserId(loanId, userId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    public Page<Loan> getUserLoans(Long userId, Pageable pageable) {
        return loanRepository.findByUserId(userId, pageable);
    }

    public Loan getActiveLoan(Long userId) {
        return loanRepository.findFirstByUserIdAndStatusInOrderByCreatedAtDesc(
                userId, List.of(Loan.LoanStatus.ACTIVE, Loan.LoanStatus.OVERDUE, Loan.LoanStatus.DISBURSED))
            .orElse(null);
    }

    @Transactional
    public Loan acceptLoan(Long userId, Long loanId, String pin) {
        Loan loan = getLoanForUser(loanId, userId);
        if (loan.getStatus() != Loan.LoanStatus.APPROVED) {
            throw new RuntimeException("Loan is not approved for acceptance");
        }

        if (pin == null || pin.isBlank()) {
            throw new RuntimeException("PIN is required");
        }

        UserAccount user = userAccountRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPin() == null || !user.getPin().equals(pin)) {
            throw new RuntimeException("Invalid PIN");
        }

        loan.setStatus(Loan.LoanStatus.ACTIVE);
        loan.setDisbursedAt(java.time.LocalDateTime.now());
        return loanRepository.save(loan);
    }

    @Transactional
    public LoanPayment recordPayment(Long loanId, Long transactionId, BigDecimal amount) {
        Loan loan = getLoan(loanId);

        LoanPayment payment = LoanPayment.builder()
            .loanId(loanId)
            .transactionId(transactionId)
            .amount(amount)
            .description("Payment for loan " + loanId)
            .build();

        payment = loanPaymentRepository.save(payment);
        log.info("Payment recorded for loan: {}", loanId);

        return payment;
    }

    public java.util.List<LoanPayment> getLoanPayments(Long loanId) {
        return loanPaymentRepository.findByLoanId(loanId);
    }

    private void validateEligibilityForApplication(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != UserAccount.UserStatus.ACTIVE) {
            throw new RuntimeException("User is blocked from new loans");
        }

        KycProfileLite kyc = kycProfileLiteRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("KYC profile not found"));

        if (kyc.getStatus() != KycProfileLite.KycStatus.APPROVED) {
            throw new RuntimeException("KYC must be approved before taking a loan");
        }
    }

    private Map<String, Object> template(String id, String code, String name, int minAmount, int maxAmount,
            int minTermDays, int maxTermDays) {
        Map<String, Object> t = new LinkedHashMap<>();
        t.put("id", id);
        t.put("code", code);
        t.put("name", name);
        t.put("category", id);
        t.put("minAmount", minAmount);
        t.put("maxAmount", maxAmount);
        t.put("minTermDays", minTermDays);
        t.put("maxTermDays", maxTermDays);
        return t;
    }
}
