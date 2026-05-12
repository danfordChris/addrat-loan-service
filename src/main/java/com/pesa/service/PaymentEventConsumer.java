package com.pesa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final LoanService loanService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(topics = "${payment.events.topic}", groupId = "${payment.events.group-id}")
    public void onPaymentEvent(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = text(root, "eventType");
            String eventId = text(root, "eventId");
            JsonNode payload = root.path("payload");

            if (eventId == null || eventId.isBlank()) {
                log.warn("Payment event missing eventId. eventType={}", eventType);
                return;
            }
            if (processedEventService.isProcessed(eventId)) {
                log.debug("Skipping already processed eventId={} eventType={}", eventId, eventType);
                return;
            }

            Long loanId = longOrNull(payload, "loanId");
            Long transactionId = longOrNull(payload, "transactionId");
            BigDecimal amount = decimalOrZero(payload, "amount");

            if (loanId == null) {
                log.warn("Payment event missing loanId. eventType={}", eventType);
                return;
            }

            switch (eventType) {
                case "payment.disbursement.posted" -> loanService.markDisbursementPosted(loanId);
                case "payment.disbursement.failed" -> loanService.markDisbursementFailed(loanId);
                case "payment.repayment.posted" -> loanService.recordPaymentFromEvent(loanId, transactionId, amount);
                case "payment.repayment.reversed" -> loanService.reversePaymentFromEvent(loanId, transactionId);
                default -> log.debug("Ignored payment eventType={}", eventType);
            }
            processedEventService.markProcessed(eventId, eventType);
        } catch (Exception e) {
            log.error("Failed to process payment event: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.path(field);
        return v.isMissingNode() || v.isNull() ? null : v.asText();
    }

    private Long longOrNull(JsonNode node, String field) {
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull()) {
            return null;
        }
        if (v.isNumber()) {
            return v.asLong();
        }
        String raw = v.asText();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Long.valueOf(raw);
    }

    private BigDecimal decimalOrZero(JsonNode node, String field) {
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull()) {
            return BigDecimal.ZERO;
        }
        if (v.isNumber()) {
            return v.decimalValue();
        }
        String raw = v.asText();
        if (raw == null || raw.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(raw);
    }
}
