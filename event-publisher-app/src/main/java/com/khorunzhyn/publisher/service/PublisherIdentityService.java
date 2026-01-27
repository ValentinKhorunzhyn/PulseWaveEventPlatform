package com.khorunzhyn.publisher.service;

import com.khorunzhyn.publisher.model.PublisherMetadata;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

@Slf4j
@Getter
@Setter
@Component
public class PublisherIdentityService {

    private final PublisherMetadata metadata;
    private final String publisherId;
    private final String publisherName;
    private final String instanceId;

    public PublisherIdentityService(
            @Value("${publisher.id:}") String configuredId,
            @Value("${publisher.name:Event Publisher}") String publisherName,
            @Value("${publisher.instance.id:1}") String instanceId) {

        this.publisherName = publisherName;
        this.instanceId = instanceId;
        this.publisherId = resolvePublisherId(configuredId);
        this.metadata = buildMetadata();
    }

    @PostConstruct
    public void init() {
        log.info("=== Publisher Identity Initialized ===");
        log.info("ID: {}", metadata.getId());
        log.info("Host: {}", metadata.getHostname());
        log.info("====================================");
    }

    private String resolvePublisherId(String configuredId) {
        return String.format("%s-%s", publisherName.toLowerCase().replace(" ", "-"), configuredId);
    }

    private PublisherMetadata buildMetadata() {
        return PublisherMetadata.builder()
                .id(publisherId)
                .name(publisherName)
                .instanceId(instanceId)
                .hostname(getHostname())
                .containerId(getContainerId())
                .javaVersion(System.getProperty("java.version"))
                .startTime(Instant.now().toString())
                .build();
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    private String getContainerId() {
        try {
            String dockerHostname = System.getenv("HOSTNAME");
            if (dockerHostname != null && dockerHostname.length() > 12) {
                return dockerHostname.substring(0, 12);
            }
            return dockerHostname != null ? dockerHostname : "not-in-container";
        } catch (Exception e) {
            return "error";
        }
    }
}
