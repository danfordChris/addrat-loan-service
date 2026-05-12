package com.pesa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal applicationFee;

    @Column(nullable = false)
    private BigDecimal monthlyInterest;

    @Column(nullable = false)
    private BigDecimal totalAmountDue;

    @Column(nullable = false)
    private Integer durationMonths;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    private LocalDateTime disbursedAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum LoanStatus {
        PENDING, APPROVED, DISBURSEMENT_PENDING, DISBURSEMENT_FAILED, ACTIVE, OVERDUE, REJECTED, DISBURSED, CLOSED, COMPLETED, DEFAULT
    }
}
