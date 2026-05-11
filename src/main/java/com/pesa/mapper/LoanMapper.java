package com.pesa.mapper;

import com.pesa.dto.LoanCalculationResponse;
import com.pesa.dto.LoanListResponse;
import com.pesa.dto.LoanPaymentResponse;
import com.pesa.dto.LoanResponse;
import com.pesa.entity.Loan;
import com.pesa.entity.LoanPayment;
import com.pesa.util.LoanCalculator;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class LoanMapper {

    public static LoanResponse toLoanResponse(Loan loan) {
        if (loan == null) {
            return null;
        }

        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .principalAmount(loan.getPrincipalAmount())
                .applicationFee(loan.getApplicationFee())
                .monthlyInterest(loan.getMonthlyInterest())
                .totalAmountDue(loan.getTotalAmountDue())
                .durationMonths(loan.getDurationMonths())
                .termDays(loan.getDurationMonths() != null ? loan.getDurationMonths() * 30 : null)
                .status(loan.getStatus() != null ? loan.getStatus().name() : null)
                .outstandingPrincipal(deriveOutstandingPrincipal(loan))
                .totalRepaid(java.math.BigDecimal.ZERO)
                .daysPastDue(deriveDaysPastDue(loan))
                .dueDate(loan.getDueDate())
                .createdAt(loan.getCreatedAt())
                .approvedAt(loan.getApprovedAt())
                .disbursedAt(loan.getDisbursedAt())
                .build();
    }

    public static LoanListResponse toLoanListResponse(Page<Loan> page) {
        if (page == null) {
            return null;
        }

        List<LoanResponse> content = page.getContent()
                .stream()
                .map(LoanMapper::toLoanResponse)
                .collect(Collectors.toList());

        return LoanListResponse.builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }

    public static LoanPaymentResponse toLoanPaymentResponse(LoanPayment payment) {
        if (payment == null) {
            return null;
        }

        return LoanPaymentResponse.builder()
                .id(payment.getId())
                .loanId(payment.getLoanId())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    public static LoanCalculationResponse toLoanCalculationResponse(LoanCalculator.LoanBreakdown breakdown) {
        if (breakdown == null) {
            return null;
        }

        return LoanCalculationResponse.builder()
                .loanAmount(breakdown.getLoanAmount())
                .applicationFee(breakdown.getApplicationFee())
                .totalInterest(breakdown.getTotalInterest())
                .totalAmountDue(breakdown.getTotalAmountDue())
                .monthlyPayment(breakdown.getMonthlyPayment())
                .disbursedAmount(breakdown.getLoanAmount().subtract(breakdown.getApplicationFee()))
                .monthlyRate(new java.math.BigDecimal("0.035"))
                .penaltyMonthlyRate(new java.math.BigDecimal("0.05"))
                .durationMonths(breakdown.getDurationMonths())
                .build();
    }

    private static java.math.BigDecimal deriveOutstandingPrincipal(Loan loan) {
        if (loan.getStatus() == Loan.LoanStatus.CLOSED || loan.getStatus() == Loan.LoanStatus.COMPLETED) {
            return java.math.BigDecimal.ZERO;
        }
        return loan.getPrincipalAmount();
    }

    private static int deriveDaysPastDue(Loan loan) {
        if (loan.getDueDate() == null) {
            return 0;
        }
        if (loan.getStatus() == Loan.LoanStatus.CLOSED || loan.getStatus() == Loan.LoanStatus.COMPLETED) {
            return 0;
        }
        if (LocalDate.now().isAfter(loan.getDueDate())) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        }
        return 0;
    }
}
