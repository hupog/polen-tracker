package com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox;

import java.util.UUID;

public record ClaimedOutboxEntry(
    UUID id, UUID requestId, String payload, UUID workerId, UUID claimToken) {}
