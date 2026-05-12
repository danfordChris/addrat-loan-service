package com.pesa.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class PaymentGatewayClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${payment.service.base-url:http://localhost:8082/api/payments}")
    private String baseUrl;

    @Value("${payment.service.default-currency:TZS}")
    private String defaultCurrency;

    @Value("${payment.service.default-channel:INTERNAL_WALLET}")
    private String defaultChannel;

    public void requestDisbursement(Long loanId,
                                    Long userId,
                                    BigDecimal amount,
                                    String bearerToken,
                                    String idempotencyKey,
                                    String correlationId) {
        String url = baseUrl + "/disburse";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearerToken != null && !bearerToken.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("loanId", loanId);
        payload.put("amount", amount);
        payload.put("currency", defaultCurrency);
        payload.put("channel", defaultChannel);
        payload.put("idempotencyKey", idempotencyKey);
        payload.put("correlationId", correlationId);
        payload.put("externalReference", "loan-" + loanId + "-user-" + userId);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(payload, headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Payment disbursement failed with status: " + response.getStatusCode());
            }
            JsonNode root = objectMapper.readTree(response.getBody());
            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                throw new RuntimeException("Payment disbursement rejected: " + root.path("message").asText("unknown"));
            }
        } catch (Exception e) {
            log.error("Disbursement request failed for loanId={}: {}", loanId, e.getMessage());
            throw new RuntimeException("Unable to initiate disbursement", e);
        }
    }
}
