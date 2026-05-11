package com.pesa.service.credit;

import com.pesa.dto.credit.CreditProfileResponse;
import com.pesa.entity.UserAccount;
import com.pesa.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditProviderClientImpl implements CreditProviderClient {

    private final UserAccountRepository userAccountRepository;

    @Value("${credit.provider.enabled:false}")
    private boolean enabled;

    @Value("${credit.provider.mock:true}")
    private boolean mock;

    @Value("${credit.provider.base-url:}")
    private String baseUrl;

    @Value("${credit.provider.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Optional<CreditProfileResponse> fetchProfile(Long userId) {
        if (!enabled) {
            return Optional.empty();
        }

        if (mock || baseUrl == null || baseUrl.isBlank()) {
            return Optional.of(mockProfile(userId));
        }

        try {
            UserAccount user = userAccountRepository.findById(userId).orElse(null);
            if (user == null) {
                return Optional.empty();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("x-api-key", apiKey);
            }

            Map<String, Object> payload = Map.of(
                    "consumerIdentity", Map.of(
                            "firstName", "N/A",
                            "surname", "N/A",
                            "dob", "1980-01-01",
                            "nationalId", String.valueOf(userId)
                    )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<CreditProfileResponse> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    request,
                    CreditProfileResponse.class
            );

            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.warn("Credit provider lookup failed for userId={}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    private CreditProfileResponse mockProfile(Long userId) {
        BigDecimal score = new BigDecimal("680");
        BigDecimal riskPct = new BigDecimal("12.5");
        BigDecimal utilization = new BigDecimal("0.32");

        return new CreditProfileResponse(
                new CreditProfileResponse.ConsumerIdentity("John", "Doe", "1980-01-01", String.valueOf(userId)),
                new CreditProfileResponse.CreditWorthiness(
                        new CreditProfileResponse.Score(score, riskPct, "MockBureau"),
                        "Approve"
                ),
                new CreditProfileResponse.Payment(
                        new CreditProfileResponse.DebtBalance(new BigDecimal("15000"), new BigDecimal("50000"), utilization),
                        new CreditProfileResponse.PaymentRemark(0, new BigDecimal("20000"))
                ),
                List.of(new CreditProfileResponse.PreviousInquiry(LocalDate.now().minusDays(20), "Mock Bank"))
        );
    }
}
