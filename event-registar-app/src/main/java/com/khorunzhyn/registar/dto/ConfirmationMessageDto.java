package com.khorunzhyn.registar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record ConfirmationMessageDto(String eventId,
                                     String publisherId,
                                     String registrationId,
                                     @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                             timezone = "UTC")
                                     Instant confirmedAt) {
}
