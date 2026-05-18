package com.khorunzhyn.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    // Железобетонная конфигурация маршрутов через Java API
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Роут 1: Для Event-API
                .route("event-api-route", r -> r
                        .path("/event-api/**")         // Ловим всё, что начинается с /event-api/
                        .filters(f -> f.stripPrefix(1)) // Отрезаем этот префикс
                        .uri("http://event-api:8090")  // Шлем в докер-контейнеры event-api
                )
                .build();
    }
}
