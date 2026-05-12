package com.pesa.service;

import com.pesa.dto.LoanStreamEvent;
import com.pesa.entity.Loan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class LoanEventStreamService {

    private static final long SSE_TIMEOUT_MILLIS = 30L * 60L * 1000L;
    private final Map<String, List<SseEmitter>> emittersByKey = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long loanId, Long userId, Loan loanSnapshot) {
        String key = key(loanId, userId);
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
        emittersByKey.computeIfAbsent(key, ignored -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(key, emitter));
        emitter.onTimeout(() -> removeEmitter(key, emitter));
        emitter.onError(error -> removeEmitter(key, emitter));

        sendEvent(key, emitter, LoanStreamEvent.statusChanged(loanSnapshot));
        return emitter;
    }

    public void publishStatusChanged(Loan loan) {
        LoanStreamEvent event = LoanStreamEvent.statusChanged(loan);
        String key = key(loan.getId(), loan.getUserId());
        List<SseEmitter> emitters = emittersByKey.getOrDefault(key, List.of());
        if (emitters.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            sendEvent(key, emitter, event);
        }
    }

    @Scheduled(fixedDelay = 15000)
    public void heartbeat() {
        emittersByKey.forEach((key, emitters) -> {
            String[] ids = key.split(":");
            Long loanId = Long.valueOf(ids[0]);
            Long userId = Long.valueOf(ids[1]);
            LoanStreamEvent heartbeatEvent = LoanStreamEvent.heartbeat(loanId, userId);
            for (SseEmitter emitter : emitters) {
                sendEvent(key, emitter, heartbeatEvent);
            }
        });
    }

    private void sendEvent(String key, SseEmitter emitter, LoanStreamEvent payload) {
        try {
            emitter.send(
                    SseEmitter.event()
                            .name(payload.type())
                            .data(payload)
            );
        } catch (IOException e) {
            removeEmitter(key, emitter);
        }
    }

    private void removeEmitter(String key, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByKey.get(key);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            emittersByKey.remove(key);
        }
    }

    private String key(Long loanId, Long userId) {
        return loanId + ":" + userId;
    }
}
