package com.khorunzhyn.publisher.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Component
public class PublisherIdentityService {

    private final String publisherId;
    private final Map<String, Object> metadata;
    private final String publisherName;
    private final String instanceId;
    private final String hostname;
    private final String containerId;

    public PublisherIdentityService(
            @Value("${publisher.id:}") String configuredId,
            @Value("${publisher.name:Event Publisher}") String publisherName,
            @Value("${publisher.instance.id:1}") String instanceId) {

        this.publisherName = publisherName;
        this.instanceId = instanceId;
        this.hostname = getHostname();
        this.containerId = getContainerId();
        this.publisherId = resolvePublisherId(configuredId);
        this.metadata = buildMetadata();
    }

    @PostConstruct
    public void init() {
        log.info("=== Publisher Identity Initialized ===");
        log.info("ID: {}", publisherId);
        log.info("Name: {}", publisherName);
        log.info("Instance: {}", instanceId);
        log.info("Host: {}", hostname);
        log.info("Container: {}", containerId);
        log.info("====================================");
    }

    private String resolvePublisherId(String configuredId) {
        return String.format("%s-%s", publisherName.toLowerCase().replace(" ", "-"), configuredId);
    }

    private Map<String, Object> buildMetadata() {
        Map<String, String> meta = new LinkedHashMap<>();
        meta.put("id", publisherId);
        meta.put("name", publisherName);
        meta.put("instance", instanceId);
        meta.put("hostname", hostname);
        meta.put("containerId", containerId);
        meta.put("startTime", Instant.now().toString());
        meta.put("javaVersion", System.getProperty("java.version"));
        return Map.copyOf(meta);
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

    public Map<String, Object> getMetadataForEvent() {
        Map<String, Object> eventMeta = new LinkedHashMap<>(metadata);
        eventMeta.put("eventTime", Instant.now().toString());
        return Map.copyOf(eventMeta);
    }
}
