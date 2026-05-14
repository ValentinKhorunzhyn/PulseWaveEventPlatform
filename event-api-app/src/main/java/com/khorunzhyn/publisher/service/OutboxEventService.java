package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.model.OutboxEvent;
import com.khorunzhyn.publisher.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository repository;

    @Transactional
    public OutboxEvent saveOutboxEvent(OutboxEvent outboxEvent) {
        return repository.save(outboxEvent);
    }

}
