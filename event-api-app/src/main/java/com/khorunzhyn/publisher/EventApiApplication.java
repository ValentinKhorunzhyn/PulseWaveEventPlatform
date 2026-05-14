package com.khorunzhyn.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventApiApplication.class);
    }
}
