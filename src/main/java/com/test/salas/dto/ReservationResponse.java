package com.test.salas.dto;

import java.time.Instant;

public record ReservationResponse(
    Long id,
    Long roomId,
    String roomName,
    String responsible,
    Instant startTime,
    Instant endTime,
    String status,
    Instant createdAt
) {}
