package com.pesa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanResponse {

    private Long id;
    private Long userId;
    private BigDecimal principalAmount;
    private BigDecimal applicationFee;
    private BigDecimal monthlyInterest;
    private BigDecimal totalAmountDue;
    private Integer durationMonths;
    private Integer termDays;
    private String status;
    private BigDecimal outstandingPrincipal;
    private BigDecimal totalRepaid;
    private Integer daysPastDue;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime disbursedAt;

    public LoanResponse() {
    }

    public LoanResponse(Long id, Long userId, BigDecimal principalAmount, BigDecimal applicationFee,
                       BigDecimal monthlyInterest, BigDecimal totalAmountDue, Integer durationMonths,
                       String status, LocalDate dueDate, LocalDateTime createdAt, LocalDateTime approvedAt,
                       LocalDateTime disbursedAt) {
        this.id = id;
        this.userId = userId;
        this.principalAmount = principalAmount;
        this.applicationFee = applicationFee;
        this.monthlyInterest = monthlyInterest;
        this.totalAmountDue = totalAmountDue;
        this.durationMonths = durationMonths;
        this.status = status;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.disbursedAt = disbursedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getApplicationFee() {
        return applicationFee;
    }

    public void setApplicationFee(BigDecimal applicationFee) {
        this.applicationFee = applicationFee;
    }

    public BigDecimal getMonthlyInterest() {
        return monthlyInterest;
    }

    public void setMonthlyInterest(BigDecimal monthlyInterest) {
        this.monthlyInterest = monthlyInterest;
    }

    public BigDecimal getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(BigDecimal totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    public Integer getDurationMonths() {
        return durationMonths;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(BigDecimal outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public BigDecimal getTotalRepaid() {
        return totalRepaid;
    }

    public void setTotalRepaid(BigDecimal totalRepaid) {
        this.totalRepaid = totalRepaid;
    }

    public Integer getDaysPastDue() {
        return daysPastDue;
    }

    public void setDaysPastDue(Integer daysPastDue) {
        this.daysPastDue = daysPastDue;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getDisbursedAt() {
        return disbursedAt;
    }

    public void setDisbursedAt(LocalDateTime disbursedAt) {
        this.disbursedAt = disbursedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private BigDecimal principalAmount;
        private BigDecimal applicationFee;
        private BigDecimal monthlyInterest;
        private BigDecimal totalAmountDue;
        private Integer durationMonths;
        private Integer termDays;
        private String status;
        private BigDecimal outstandingPrincipal;
        private BigDecimal totalRepaid;
        private Integer daysPastDue;
        private LocalDate dueDate;
        private LocalDateTime createdAt;
        private LocalDateTime approvedAt;
        private LocalDateTime disbursedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder principalAmount(BigDecimal principalAmount) {
            this.principalAmount = principalAmount;
            return this;
        }

        public Builder applicationFee(BigDecimal applicationFee) {
            this.applicationFee = applicationFee;
            return this;
        }

        public Builder monthlyInterest(BigDecimal monthlyInterest) {
            this.monthlyInterest = monthlyInterest;
            return this;
        }

        public Builder totalAmountDue(BigDecimal totalAmountDue) {
            this.totalAmountDue = totalAmountDue;
            return this;
        }

        public Builder durationMonths(Integer durationMonths) {
            this.durationMonths = durationMonths;
            return this;
        }

        public Builder termDays(Integer termDays) {
            this.termDays = termDays;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder outstandingPrincipal(BigDecimal outstandingPrincipal) {
            this.outstandingPrincipal = outstandingPrincipal;
            return this;
        }

        public Builder totalRepaid(BigDecimal totalRepaid) {
            this.totalRepaid = totalRepaid;
            return this;
        }

        public Builder daysPastDue(Integer daysPastDue) {
            this.daysPastDue = daysPastDue;
            return this;
        }

        public Builder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder approvedAt(LocalDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public Builder disbursedAt(LocalDateTime disbursedAt) {
            this.disbursedAt = disbursedAt;
            return this;
        }

        public LoanResponse build() {
            LoanResponse response = new LoanResponse(id, userId, principalAmount, applicationFee, monthlyInterest,
                    totalAmountDue, durationMonths, status, dueDate, createdAt, approvedAt, disbursedAt);
            response.setTermDays(termDays);
            response.setOutstandingPrincipal(outstandingPrincipal);
            response.setTotalRepaid(totalRepaid);
            response.setDaysPastDue(daysPastDue);
            return response;
        }
    }
}
