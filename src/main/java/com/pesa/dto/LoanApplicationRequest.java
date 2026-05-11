package com.pesa.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class LoanApplicationRequest {

    private String productId;

    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than 0")
    private Integer termDays;

    private Integer durationMonths;

    private String purpose;

    public LoanApplicationRequest() {
    }

    public LoanApplicationRequest(BigDecimal amount, Integer durationMonths) {
        this.amount = amount;
        this.durationMonths = durationMonths;
        this.termDays = durationMonths * 30;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getDurationMonths() {
        if (durationMonths != null && durationMonths > 0) {
            return durationMonths;
        }
        if (termDays != null && termDays > 0) {
            return Math.max(1, (int) Math.ceil(termDays / 30.0));
        }
        return null;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Integer getTermDays() {
        return termDays;
    }

    public void setTermDays(Integer termDays) {
        this.termDays = termDays;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
