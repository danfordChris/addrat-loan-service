package com.pesa.dto.credit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreditProfileResponse(
        ConsumerIdentity consumerIdentity,
        CreditWorthiness creditWorthiness,
        Payment payment,
        List<PreviousInquiry> previousInquiries
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ConsumerIdentity(
            String firstName,
            String surname,
            String dob,
            String nationalId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CreditWorthiness(
            Score score,
            String recommendation
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Score(
            BigDecimal value,
            BigDecimal riskPercentage,
            String provider
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Payment(
            DebtBalance debtBalance,
            PaymentRemark paymentRemark
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DebtBalance(
            BigDecimal currentDebt,
            BigDecimal totalCreditLimit,
            BigDecimal utilizationRatio
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentRemark(
            Integer numberOfDefaults,
            BigDecimal highestDebt
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PreviousInquiry(
            LocalDate date,
            String inquirerName
    ) {}
}
