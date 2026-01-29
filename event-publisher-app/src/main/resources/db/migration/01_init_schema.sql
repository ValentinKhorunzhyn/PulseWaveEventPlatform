CREATE SCHEMA IF NOT EXISTS publisher;

CREATE TABLE IF NOT EXISTS table_events (
    id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    payload TEXT,
    status VARCHAR(20) NOT NULL,
    publisher_id VARCHAR(255),
    publisher_metadata JSONB,
    created_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT
);