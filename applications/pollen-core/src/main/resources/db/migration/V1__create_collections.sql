CREATE TABLE collections
(
    id             UUID PRIMARY KEY,
    source_type    VARCHAR(40) NOT NULL,
    status         VARCHAR(40) NOT NULL,
    requested_at   TIMESTAMPTZ NOT NULL,
    completed_at   TIMESTAMPTZ,
    produced_items INTEGER     NOT NULL DEFAULT 0
);
