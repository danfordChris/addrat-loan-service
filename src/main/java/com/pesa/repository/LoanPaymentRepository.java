package com.pesa.repository;

import com.pesa.entity.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {
    List<LoanPayment> findByLoanId(Long loanId);
    Optional<LoanPayment> findByTransactionId(Long transactionId);
    long deleteByTransactionId(Long transactionId);

    @Query("select coalesce(sum(lp.amount),0) from LoanPayment lp where lp.loanId = :loanId")
    BigDecimal sumAmountsByLoanId(Long loanId);
}
