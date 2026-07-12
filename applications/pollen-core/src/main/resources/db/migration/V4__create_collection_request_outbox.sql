CREATE TABLE collection_request_outbox
(
    id           UUID PRIMARY KEY,
    request_id   UUID         NOT NULL UNIQUE REFERENCES collections (id),
    event_type   VARCHAR(160) NOT NULL,
    payload      TEXT         NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    attempts     INTEGER      NOT NULL DEFAULT 0,
    available_at TIMESTAMPTZ  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    published_at TIMESTAMPTZ,
    last_error   TEXT
);

CREATE INDEX idx_collection_request_outbox_pending
    ON collection_request_outbox (available_at, created_at)
    WHERE status = 'PENDING';
