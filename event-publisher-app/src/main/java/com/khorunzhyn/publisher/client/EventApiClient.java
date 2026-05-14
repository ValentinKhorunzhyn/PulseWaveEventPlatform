package com.khorunzhyn.publisher.client;

import com.khorunzhyn.publisher.config.FeignClientConfig;
import com.khorunzhyn.publisher.dto.EventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Пока балансировщика нет, обращаемся к event-api по имени сервиса в Докере
@FeignClient(
        name = "event-api-client",
        url = "${EVENT_API_URL:http://event-api:8090}",
        configuration = FeignClientConfig.class
)
public interface EventApiClient {

    @PostMapping("/api/v1/event")
    void sendEvent(@RequestBody EventDto eventDto);
}
