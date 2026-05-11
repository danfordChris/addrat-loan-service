package com.pesa.repository;

import com.pesa.entity.KycProfileLite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycProfileLiteRepository extends JpaRepository<KycProfileLite, Long> {
    Optional<KycProfileLite> findByUserId(Long userId);
}
