package com.pesa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "credit_board_scores")
public class CreditBoardScoreLite {

    @Id
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    private BigDecimal score;

    @Column(name = "loan_limit")
    private BigDecimal loanLimit;

    private Boolean eligible;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public BigDecimal getLoanLimit() {
        return loanLimit;
    }

    public Boolean getEligible() {
        return eligible;
    }
}
