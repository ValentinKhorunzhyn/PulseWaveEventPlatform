ALTER TABLE table_reg_events
ADD COLUMN IF NOT EXISTS confirmation_sent_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_reg_events_status_conf_sent
ON registered_events (processing_status, confirmation_sent_at);