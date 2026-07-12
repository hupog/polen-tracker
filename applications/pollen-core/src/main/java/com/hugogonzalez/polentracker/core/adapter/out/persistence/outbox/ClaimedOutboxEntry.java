package com.hugogonzalez.polentracker.core.adapter.out.persistence.outbox;

import java.util.UUID;

public record ClaimedOutboxEntry(UUID id, String payload, UUID workerId) {}
