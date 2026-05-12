CREATE TABLE IF NOT EXISTS processed_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(120) NOT NULL,
    event_type VARCHAR(120) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (event_id)
);

CREATE INDEX IF NOT EXISTS idx_processed_events_type ON processed_events(event_type);
