CREATE TABLE rabbit_message_trace
(
    id                UUID PRIMARY KEY,
    collection_id     UUID         NOT NULL REFERENCES collections (id),
    message_id        UUID,
    direction         VARCHAR(20)  NOT NULL,
    event_type        VARCHAR(120) NOT NULL,
    status            VARCHAR(30)  NOT NULL,
    exchange_name    VARCHAR(255),
    routing_key       VARCHAR(255),
    queue_name        VARCHAR(255),
    delivery_tag      BIGINT,
    consumer_tag      VARCHAR(255),
    correlation_id    VARCHAR(255),
    redelivered       BOOLEAN      NOT NULL DEFAULT FALSE,
    headers           JSONB        NOT NULL DEFAULT '{}'::jsonb,
    payload           TEXT         NOT NULL,
    occurred_at       TIMESTAMPTZ  NOT NULL,
    processed_at      TIMESTAMPTZ,
    error_message     TEXT
);

CREATE INDEX idx_rabbit_message_trace_collection ON rabbit_message_trace (collection_id, occurred_at);
CREATE INDEX idx_rabbit_message_trace_message ON rabbit_message_trace (message_id);
