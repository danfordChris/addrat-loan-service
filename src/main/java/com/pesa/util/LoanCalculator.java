package com.pesa.util;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LoanCalculator {

    private static final BigDecimal APP_FEE_RATE = new BigDecimal("0.05");
    private static final BigDecimal MONTHLY_INTEREST_RATE = new BigDecimal("0.035");
    private static final BigDecimal MONTHLY_PENALTY_RATE = new BigDecimal("0.05");

    public LoanBreakdown calculate(BigDecimal loanAmount, int durationMonths) {
        BigDecimal applicationFee = loanAmount.multiply(APP_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int i = 0; i < durationMonths; i++) {
            BigDecimal remainingBalance = loanAmount.subtract(
                loanAmount.multiply(new BigDecimal(i)).divide(new BigDecimal(durationMonths), 2, RoundingMode.HALF_UP)
            );
            totalInterest = totalInterest.add(
                remainingBalance.multiply(MONTHLY_INTEREST_RATE).setScale(2, RoundingMode.HALF_UP)
            );
        }

        BigDecimal totalDue = loanAmount.add(applicationFee).add(totalInterest)
            .setScale(2, RoundingMode.HALF_UP);

        return LoanBreakdown.builder()
            .loanAmount(loanAmount)
            .applicationFee(applicationFee)
            .totalInterest(totalInterest)
            .totalAmountDue(totalDue)
            .monthlyPayment(totalDue.divide(new BigDecimal(durationMonths), 2, RoundingMode.HALF_UP))
            .durationMonths(durationMonths)
            .build();
    }

    @Data
    @Builder
    public static class LoanBreakdown {
        private BigDecimal loanAmount;
        private BigDecimal applicationFee;
        private BigDecimal totalInterest;
        private BigDecimal totalAmountDue;
        private BigDecimal monthlyPayment;
        private Integer durationMonths;
    }
}
