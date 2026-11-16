package com.test.salas.dto.mapper;

import org.springframework.stereotype.Component;

import com.test.salas.dto.ReservationResponse;
import com.test.salas.entity.Reservation;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getRoom().getId(),
            reservation.getRoom().getName(),
            reservation.getResponsible(),
            reservation.getStartTime(),
            reservation.getEndTime(),
            reservation.getStatus().name(),
            reservation.getCreatedAt()
        );
    }
}
