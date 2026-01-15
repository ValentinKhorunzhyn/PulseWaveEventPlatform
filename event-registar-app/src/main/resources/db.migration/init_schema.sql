CREATE SCHEMA IF NOT EXISTS registar;
   -- Create registered_events table
CREATE TABLE IF NOT EXISTS registered_events (
    id UUID PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL UNIQUE,
    source_publisher_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    publisher_metadata JSONB,
    original_created_at TIMESTAMP NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    processing_status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    confirmation_id VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP
);