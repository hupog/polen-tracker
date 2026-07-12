ALTER TABLE collection_request_outbox
    ADD COLUMN claim_token UUID;

ALTER TABLE collection_request_outbox
    ADD CONSTRAINT ck_outbox_claim_fields
        CHECK (
            (status = 'PROCESSING'
                AND locked_by IS NOT NULL
                AND locked_until IS NOT NULL
                AND claim_token IS NOT NULL)
            OR
            (status <> 'PROCESSING'
                AND locked_by IS NULL
                AND locked_until IS NULL
                AND claim_token IS NULL)
        );
