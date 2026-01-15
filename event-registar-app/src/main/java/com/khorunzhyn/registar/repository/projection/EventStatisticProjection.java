package com.khorunzhyn.registar.repository.projection;

public interface EventStatisticProjection {
    Long getTotalEvents();
    Long getProcessedEvents();
    Long getConfirmedEvents();
    Long getFailedEvents();
    Long getDuplicateEvents();
}
