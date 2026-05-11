package com.pesa.repository;

import com.pesa.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Page<Loan> findByUserId(Long userId, Pageable pageable);
    Optional<Loan> findByIdAndUserId(Long id, Long userId);
    Optional<Loan> findFirstByUserIdAndStatusInOrderByCreatedAtDesc(Long userId, Collection<Loan.LoanStatus> statuses);
}
