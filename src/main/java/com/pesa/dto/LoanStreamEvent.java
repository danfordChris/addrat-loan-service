package com.pesa.dto;

import com.pesa.entity.Loan;
import lombok.Builder;

import java.time.Instant;

@Builder
public record LoanStreamEvent(
        String type,
        Long loanId,
        Long userId,
        String status,
        Instant timestamp
) {
    public static LoanStreamEvent statusChanged(Loan loan) {
        return LoanStreamEvent.builder()
                .type("LOAN_STATUS_CHANGED")
                .loanId(loan.getId())
                .userId(loan.getUserId())
                .status(loan.getStatus().name())
                .timestamp(Instant.now())
                .build();
    }

    public static LoanStreamEvent heartbeat(Long loanId, Long userId) {
        return LoanStreamEvent.builder()
                .type("HEARTBEAT")
                .loanId(loanId)
                .userId(userId)
                .timestamp(Instant.now())
                .build();
    }
}
