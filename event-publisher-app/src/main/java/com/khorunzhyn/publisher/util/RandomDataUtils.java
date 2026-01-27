package com.khorunzhyn.publisher.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class RandomDataUtils {

    // --- User Action Data ---
    public static final List<String> USER_ACTIONS = List.of(
            "LOGIN", "LOGOUT", "VIEW_PAGE", "CREATE_RESOURCE",
            "UPDATE_RESOURCE", "DELETE_RESOURCE", "DOWNLOAD_FILE"
    );

    public static final List<String> ACTOR_TYPES = List.of("CUSTOMER", "ADMIN", "SUPPORT", "API_CLIENT");
    public static final List<String> USER_ROLES = List.of("USER", "VIEWER", "EDITOR", "ADMIN");
    public static final List<String> TARGET_TYPES = List.of("WEB_PAGE", "API_ENDPOINT", "FILE", "DATABASE_RECORD");
    public static final List<String> LOCATIONS = List.of("US", "EU", "ASIA", "UNKNOWN");

    // --- System Alert Data ---
    public static final List<String> SYSTEM_COMPONENTS = List.of("DATABASE", "API_GATEWAY", "CACHE", "QUEUE", "STORAGE");
    public static final List<String> ALERT_TYPES = List.of("HIGH_CPU", "MEMORY_LEAK", "DISK_FULL", "HIGH_LATENCY", "ERROR_RATE_SPIKE");
    public static final List<String> RECOMMENDATIONS = List.of("Scale up instance", "Restart service", "Check dependencies", "Check logs");

    // --- Audit Data ---
    public static final List<String> AUDIT_ACTIONS = List.of("DATA_ACCESS", "CONFIG_CHANGE", "PERMISSION_UPDATE", "SECURITY_EVENT");
    public static final List<String> AUDIT_ENTITY_TYPES = List.of("USER", "ORDER", "PAYMENT", "SETTING", "ROLE");
    public static final List<String> AUDIT_FIELDS = List.of("status", "permissions", "email", "amount", "config");
    public static final List<String> AUDIT_REASONS = List.of("user_request", "system_update", "bug_fix", "compliance");
    public static final List<String> COMPLIANCE_STANDARDS = List.of("GDPR", "HIPAA", "PCI_DSS", "SOX", "ISO27001");

    // --- Business Data ---
    public static final List<String> CURRENCIES = List.of("USD", "EUR", "GBP", "JPY");
    public static final List<String> ORDER_STATUSES = List.of("PENDING", "PROCESSING", "SHIPPED", "DELIVERED");
    public static final List<String> PAYMENT_METHODS = List.of("CREDIT_CARD", "PAYPAL", "BANK_TRANSFER", "APPLE_PAY");
    public static final List<String> CUSTOMER_NAMES = List.of("John Smith", "Jane Doe", "Alex Johnson", "Maria Garcia", "Chris Lee");
    public static final List<String> CUSTOMER_TIERS = List.of("BASIC", "PREMIUM", "ENTERPRISE", "GOLD");

    // --- User Agents ---
    public static final List<String> USER_AGENTS = List.of(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15",
            "PostmanRuntime/7.29.0"
    );

    // --- Core Generators ---

    public static <T> T randomItem(List<T> elements) {
        return elements.get(ThreadLocalRandom.current().nextInt(elements.size()));
    }

    @SafeVarargs
    public static <T> T randomItem(T... elements) {
        return elements[ThreadLocalRandom.current().nextInt(elements.length)];
    }

    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static double randomDouble(double min, double max) {
        return min + (max - min) * ThreadLocalRandom.current().nextDouble();
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }


    // Generate id, example: "user-1234"
    public static String randomId(String prefix, int min, int max) {
        return prefix + randomInt(min, max);
    }

    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    public static String generateIpAddress() {
        return String.format("%d.%d.%d.%d",
                randomInt(0, 255), randomInt(0, 255),
                randomInt(0, 255), randomInt(0, 255)
        );
    }
}