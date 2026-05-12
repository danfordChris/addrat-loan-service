package com.pesa.util;

import com.pesa.config.LoanPolicyProperties;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LoanCalculator {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private final LoanPolicyProperties loanPolicyProperties;

    public LoanCalculator(LoanPolicyProperties loanPolicyProperties) {
        this.loanPolicyProperties = loanPolicyProperties;
    }

    public LoanBreakdown calculate(BigDecimal loanAmount, int durationMonths) {
        BigDecimal applicationFeeRate = loanPolicyProperties.getApplicationFeePct().divide(HUNDRED, 8, RoundingMode.HALF_UP);
        BigDecimal monthlyInterestRate = loanPolicyProperties.getMonthlyInterestRatePct().divide(HUNDRED, 8, RoundingMode.HALF_UP);

        BigDecimal applicationFee = loanAmount.multiply(applicationFeeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int i = 0; i < durationMonths; i++) {
            BigDecimal remainingBalance = loanAmount.subtract(
                loanAmount.multiply(new BigDecimal(i)).divide(new BigDecimal(durationMonths), 2, RoundingMode.HALF_UP)
            );
            totalInterest = totalInterest.add(
                remainingBalance.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP)
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
