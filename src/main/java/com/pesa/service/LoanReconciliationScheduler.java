package com.pesa.service;

import com.pesa.entity.Loan;
import com.pesa.repository.LoanPaymentRepository;
import com.pesa.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanReconciliationScheduler {

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;

    @Scheduled(cron = "${loan.reconciliation.cron:0 */30 * * * *}")
    @Transactional
    public void reconcileLoanBalances() {
        List<Loan> loans = loanRepository.findByStatusIn(List.of(
                Loan.LoanStatus.ACTIVE,
                Loan.LoanStatus.OVERDUE,
                Loan.LoanStatus.CLOSED,
                Loan.LoanStatus.COMPLETED
        ));

        for (Loan loan : loans) {
            BigDecimal totalPaid = loanPaymentRepository.sumAmountsByLoanId(loan.getId());
            if (totalPaid == null) {
                totalPaid = BigDecimal.ZERO;
            }

            if (totalPaid.compareTo(loan.getTotalAmountDue()) >= 0
                    && loan.getStatus() != Loan.LoanStatus.CLOSED
                    && loan.getStatus() != Loan.LoanStatus.COMPLETED) {
                loan.setStatus(Loan.LoanStatus.CLOSED);
                loan.setCompletedAt(LocalDateTime.now());
                loanRepository.save(loan);
                log.warn("Reconciliation closed loan. loanId={}, totalPaid={}, totalDue={}",
                        loan.getId(), totalPaid, loan.getTotalAmountDue());
                continue;
            }

            if (totalPaid.compareTo(loan.getTotalAmountDue()) < 0
                    && (loan.getStatus() == Loan.LoanStatus.CLOSED || loan.getStatus() == Loan.LoanStatus.COMPLETED)) {
                loan.setStatus(Loan.LoanStatus.ACTIVE);
                loan.setCompletedAt(null);
                loanRepository.save(loan);
                log.warn("Reconciliation reopened loan. loanId={}, totalPaid={}, totalDue={}",
                        loan.getId(), totalPaid, loan.getTotalAmountDue());
            }
        }
    }
}
