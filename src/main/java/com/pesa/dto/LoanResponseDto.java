package com.pesa.dto;

import com.pesa.entity.Loan;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoanResponseDto {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Map<String, Object> from(Loan loan, double monthlyInterestRate) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", String.valueOf(loan.getId()));
        m.put("status", loan.getStatus().name());
        m.put("principalAmount", loan.getPrincipalAmount());
        m.put("outstandingAmount", loan.getTotalAmountDue());
        m.put("totalAmountDue", loan.getTotalAmountDue());
        m.put("applicationFee", loan.getApplicationFee());
        m.put("monthlyInterest", loan.getMonthlyInterest());
        m.put("interestRate", monthlyInterestRate);
        m.put("termMonths", loan.getDurationMonths());
        m.put("durationMonths", loan.getDurationMonths());
        m.put("nextDueDate", loan.getDueDate() != null ? loan.getDueDate().format(ISO) : null);
        m.put("dueDate", loan.getDueDate() != null ? loan.getDueDate().format(ISO) : null);
        m.put("disbursedAt", loan.getDisbursedAt() != null ? loan.getDisbursedAt().format(ISO_DT) : null);
        m.put("approvedAt", loan.getApprovedAt() != null ? loan.getApprovedAt().format(ISO_DT) : null);
        m.put("createdAt", loan.getCreatedAt() != null ? loan.getCreatedAt().format(ISO_DT) : null);
        m.put("userId", loan.getUserId());
        return m;
    }
}
