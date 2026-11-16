package com.test.salas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.salas.Exceptions.ResourceNotFoundException;
import com.test.salas.Exceptions.RoomOccupiedException;
import com.test.salas.dto.ReservationRequest;
import com.test.salas.dto.ReservationResponse;
import com.test.salas.dto.mapper.ReservationMapper;
import com.test.salas.entity.Reservation;
import com.test.salas.entity.ReservationStatus;
import com.test.salas.entity.Room;
import com.test.salas.repository.ReservationRepository;

import java.time.Instant;

@Service
public class ReservationServiceImp implements IReservationService{

    private final ReservationRepository reservationRepository;
    private final IRoomService roomService;
    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationServiceImp(ReservationRepository reservationRepository,
                              IRoomService roomService,
                              ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
        this.reservationMapper = reservationMapper;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        // 1. Validar regla básica de negocio
        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // 2. Verificar sala
        Room room = roomService.getRoomOrThrow(request.roomId());

        // 3. Verificar solapamiento
        boolean occupied = reservationRepository.existsOverlappingReservation(
            room.getId(), request.startTime(), request.endTime()
        );
        if (occupied) {
            throw new RoomOccupiedException(
                "La sala ya está reservada en el horario solicitado");
        }

        // 4. Crear y guardar
        Reservation reservation = new Reservation(
            room, request.responsible(), request.startTime(), request.endTime()
        );
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("La reserva ya está cancelada");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> listReservations(
            Long roomId, Instant from, Instant to, Pageable pageable) {
        return reservationRepository.findByFilters(roomId, from, to, pageable)
            .map(reservationMapper::toResponse);
    }
}