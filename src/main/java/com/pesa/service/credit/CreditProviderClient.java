package com.pesa.service.credit;

import com.pesa.dto.credit.CreditProfileResponse;

import java.util.Optional;

public interface CreditProviderClient {
    Optional<CreditProfileResponse> fetchProfile(Long userId);
}
