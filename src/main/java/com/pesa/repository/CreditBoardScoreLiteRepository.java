package com.pesa.repository;

import com.pesa.entity.CreditBoardScoreLite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditBoardScoreLiteRepository extends JpaRepository<CreditBoardScoreLite, Long> {
    Optional<CreditBoardScoreLite> findByUserId(Long userId);
}
