package com.khorunzhyn.registar.task;

import com.khorunzhyn.registar.enums.EventStatus;
import com.khorunzhyn.registar.model.RegisteredEvent;
import com.khorunzhyn.registar.repository.RegisteredEventRepository;
import com.khorunzhyn.registar.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmationTask {

    private final RegisteredEventRepository eventRepository;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelayString = "${registar.confirmation.interval:5000}")
    public void processPendingConfirmations() {

        List<RegisteredEvent> pendingEvents = eventRepository.findByProcessingStatusAndConfirmationSentAtIsNull(
                EventStatus.CONFIRMED,
                PageRequest.of(0, 50)
        );

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.debug("Found {} events pending confirmation", pendingEvents.size());

        for (RegisteredEvent event : pendingEvents) {
            try {
                sendAndUpdate(event);
            } catch (Exception e) {
                log.error("Failed to send confirmation for event {}. Will retry later.", event.getEventId(), e);
            }
        }
    }

    private void sendAndUpdate(RegisteredEvent event) throws ExecutionException, InterruptedException {
        // 1. Sync call (can throw exception)
        kafkaProducerService.sendConfirmationSync(event);
        // 2. Update event
        event.setConfirmationSentAt(Instant.now());
        eventRepository.save(event);
    }

}
