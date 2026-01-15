package com.khorunzhyn.publisher.enums;

public enum EventType {
    USER_ACTION,
    SYSTEM_ALERT,
    BUSINESS_EVENT;

    private static final EventType[] VALUES = values();

    public static EventType getRandomEventType() {
        return VALUES[(int) (Math.random() * VALUES.length)];
    }

}
