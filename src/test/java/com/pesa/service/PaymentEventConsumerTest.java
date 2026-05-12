package com.pesa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class PaymentEventConsumerTest {

    private LoanService loanService;
    private ProcessedEventService processedEventService;
    private PaymentEventConsumer consumer;

    @BeforeEach
    void setUp() {
        loanService = mock(LoanService.class);
        processedEventService = mock(ProcessedEventService.class);
        consumer = new PaymentEventConsumer(new ObjectMapper(), loanService, processedEventService);
    }

    @Test
    void shouldSkipAlreadyProcessedEvent() {
        when(processedEventService.isProcessed("123")).thenReturn(true);

        String message = """
                {
                  "eventId":"123",
                  "eventType":"payment.repayment.posted",
                  "payload":{"loanId":10,"transactionId":88,"amount":50000}
                }
                """;

        consumer.onPaymentEvent(message);

        verify(loanService, never()).recordPaymentFromEvent(anyLong(), anyLong(), any(BigDecimal.class));
        verify(processedEventService, never()).markProcessed(anyString(), anyString());
    }

    @Test
    void shouldProcessRepaymentPostedAndMarkProcessed() {
        when(processedEventService.isProcessed("evt-1")).thenReturn(false);

        String message = """
                {
                  "eventId":"evt-1",
                  "eventType":"payment.repayment.posted",
                  "payload":{"loanId":10,"transactionId":88,"amount":50000}
                }
                """;

        consumer.onPaymentEvent(message);

        verify(loanService).recordPaymentFromEvent(10L, 88L, new BigDecimal("50000"));
        verify(processedEventService).markProcessed("evt-1", "payment.repayment.posted");
    }

    @Test
    void shouldProcessReversedEvent() {
        when(processedEventService.isProcessed("evt-2")).thenReturn(false);

        String message = """
                {
                  "eventId":"evt-2",
                  "eventType":"payment.repayment.reversed",
                  "payload":{"loanId":10,"transactionId":88}
                }
                """;

        consumer.onPaymentEvent(message);

        verify(loanService).reversePaymentFromEvent(10L, 88L);
        verify(processedEventService).markProcessed("evt-2", "payment.repayment.reversed");
    }

    @Test
    void shouldNotMarkProcessedWhenBusinessHandlingFails() {
        when(processedEventService.isProcessed("evt-3")).thenReturn(false);
        doThrow(new RuntimeException("Invalid loan transition"))
                .when(loanService).markDisbursementPosted(10L);

        String message = """
                {
                  "eventId":"evt-3",
                  "eventType":"payment.disbursement.posted",
                  "payload":{"loanId":10}
                }
                """;

        try {
            consumer.onPaymentEvent(message);
        } catch (RuntimeException ignored) {
        }

        verify(processedEventService, never()).markProcessed("evt-3", "payment.disbursement.posted");
    }
}
