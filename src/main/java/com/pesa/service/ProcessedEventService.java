package com.pesa.service;

import com.pesa.entity.ProcessedEvent;
import com.pesa.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessedEventService {

    private final ProcessedEventRepository processedEventRepository;

    public boolean isProcessed(String eventId) {
        return processedEventRepository.existsByEventId(eventId);
    }

    @Transactional
    public void markProcessed(String eventId, String eventType) {
        if (processedEventRepository.existsByEventId(eventId)) {
            return;
        }
        processedEventRepository.save(ProcessedEvent.builder()
                .eventId(eventId)
                .eventType(eventType == null ? "unknown" : eventType)
                .build());
    }
}
