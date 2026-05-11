package com.pesa.dto;

import java.math.BigDecimal;

public class LoanCalculationResponse {

    private BigDecimal loanAmount;
    private BigDecimal applicationFee;
    private BigDecimal totalInterest;
    private BigDecimal totalAmountDue;
    private BigDecimal monthlyPayment;
    private BigDecimal disbursedAmount;
    private BigDecimal monthlyRate;
    private BigDecimal penaltyMonthlyRate;
    private Integer durationMonths;

    public LoanCalculationResponse() {
    }

    public LoanCalculationResponse(BigDecimal loanAmount, BigDecimal applicationFee, BigDecimal totalInterest,
                                   BigDecimal totalAmountDue, BigDecimal monthlyPayment, Integer durationMonths) {
        this.loanAmount = loanAmount;
        this.applicationFee = applicationFee;
        this.totalInterest = totalInterest;
        this.totalAmountDue = totalAmountDue;
        this.monthlyPayment = monthlyPayment;
        this.durationMonths = durationMonths;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getApplicationFee() {
        return applicationFee;
    }

    public void setApplicationFee(BigDecimal applicationFee) {
        this.applicationFee = applicationFee;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(BigDecimal totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getDisbursedAmount() {
        return disbursedAmount;
    }

    public void setDisbursedAmount(BigDecimal disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public BigDecimal getPenaltyMonthlyRate() {
        return penaltyMonthlyRate;
    }

    public void setPenaltyMonthlyRate(BigDecimal penaltyMonthlyRate) {
        this.penaltyMonthlyRate = penaltyMonthlyRate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal loanAmount;
        private BigDecimal applicationFee;
        private BigDecimal totalInterest;
        private BigDecimal totalAmountDue;
        private BigDecimal monthlyPayment;
        private BigDecimal disbursedAmount;
        private BigDecimal monthlyRate;
        private BigDecimal penaltyMonthlyRate;
        private Integer durationMonths;

        public Builder loanAmount(BigDecimal loanAmount) {
            this.loanAmount = loanAmount;
            return this;
        }

        public Builder applicationFee(BigDecimal applicationFee) {
            this.applicationFee = applicationFee;
            return this;
        }

        public Builder totalInterest(BigDecimal totalInterest) {
            this.totalInterest = totalInterest;
            return this;
        }

        public Builder totalAmountDue(BigDecimal totalAmountDue) {
            this.totalAmountDue = totalAmountDue;
            return this;
        }

        public Builder monthlyPayment(BigDecimal monthlyPayment) {
            this.monthlyPayment = monthlyPayment;
            return this;
        }

        public Builder durationMonths(Integer durationMonths) {
            this.durationMonths = durationMonths;
            return this;
        }

        public Builder disbursedAmount(BigDecimal disbursedAmount) {
            this.disbursedAmount = disbursedAmount;
            return this;
        }

        public Builder monthlyRate(BigDecimal monthlyRate) {
            this.monthlyRate = monthlyRate;
            return this;
        }

        public Builder penaltyMonthlyRate(BigDecimal penaltyMonthlyRate) {
            this.penaltyMonthlyRate = penaltyMonthlyRate;
            return this;
        }

        public LoanCalculationResponse build() {
            LoanCalculationResponse response = new LoanCalculationResponse(loanAmount, applicationFee, totalInterest,
                    totalAmountDue, monthlyPayment, durationMonths);
            response.setDisbursedAmount(disbursedAmount);
            response.setMonthlyRate(monthlyRate);
            response.setPenaltyMonthlyRate(penaltyMonthlyRate);
            return response;
        }
    }
}
