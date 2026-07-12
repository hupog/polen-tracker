ALTER TABLE collection_request_outbox
    ADD COLUMN locked_by UUID,
    ADD COLUMN locked_until TIMESTAMPTZ;

DROP INDEX idx_collection_request_outbox_pending;

CREATE INDEX idx_collection_request_outbox_available
    ON collection_request_outbox (status, available_at, locked_until, created_at)
    WHERE status IN ('PENDING', 'PROCESSING');
