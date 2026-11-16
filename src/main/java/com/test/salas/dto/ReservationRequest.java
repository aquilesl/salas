package com.test.salas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record ReservationRequest(
    @NotNull(message = "El ID de la sala es obligatorio")
    Long roomId,

    @NotBlank(message = "El responsable es obligatorio")
    @Size(max = 255, message = "El responsable no puede exceder 255 caracteres")
    String responsible,

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    Instant startTime,

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    Instant endTime
) {}
