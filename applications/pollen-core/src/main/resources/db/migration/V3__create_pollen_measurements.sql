CREATE TABLE pollen_measurements
(
    id              UUID PRIMARY KEY,
    collection_id   UUID           NOT NULL REFERENCES collections (id),
    event_id         UUID           NOT NULL UNIQUE,
    logical_key      VARCHAR(500)   NOT NULL,
    source_type      VARCHAR(40)    NOT NULL,
    location_name    VARCHAR(255),
    latitude         DOUBLE PRECISION NOT NULL,
    longitude        DOUBLE PRECISION NOT NULL,
    pollen_type      VARCHAR(40)    NOT NULL,
    metric_type      VARCHAR(40)    NOT NULL,
    value            NUMERIC(18, 6) NOT NULL,
    unit             VARCHAR(50)    NOT NULL,
    data_nature      VARCHAR(40)    NOT NULL,
    valid_at         TIMESTAMPTZ    NOT NULL,
    collected_at     TIMESTAMPTZ    NOT NULL,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT uq_pollen_measurement_collection_key UNIQUE (collection_id, logical_key)
);

CREATE INDEX idx_pollen_measurements_collection_time
    ON pollen_measurements (collection_id, valid_at);

CREATE INDEX idx_pollen_measurements_location_time
    ON pollen_measurements (latitude, longitude, valid_at);

CREATE INDEX idx_pollen_measurements_type_time
    ON pollen_measurements (pollen_type, valid_at);
