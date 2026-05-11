package com.pesa.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanPaymentResponse {

    private Long id;
    private Long loanId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime paymentDate;

    public LoanPaymentResponse() {
    }

    public LoanPaymentResponse(Long id, Long loanId, BigDecimal amount, String description, LocalDateTime paymentDate) {
        this.id = id;
        this.loanId = loanId;
        this.amount = amount;
        this.description = description;
        this.paymentDate = paymentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long loanId;
        private BigDecimal amount;
        private String description;
        private LocalDateTime paymentDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder loanId(Long loanId) {
            this.loanId = loanId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder paymentDate(LocalDateTime paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public LoanPaymentResponse build() {
            return new LoanPaymentResponse(id, loanId, amount, description, paymentDate);
        }
    }
}
