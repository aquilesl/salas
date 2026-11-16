package com.test.salas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.test.salas.Exceptions.RoomOccupiedException;
import com.test.salas.dto.ReservationRequest;
import com.test.salas.dto.ReservationResponse;
import com.test.salas.dto.mapper.ReservationMapper;
import com.test.salas.entity.Reservation;
import com.test.salas.entity.ReservationStatus;
import com.test.salas.entity.Room;
import com.test.salas.repository.ReservationRepository;
import com.test.salas.service.IReservationService;
import com.test.salas.service.IRoomService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private IRoomService roomService;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private IReservationService reservationService;

    @Test
    void shouldThrowWhenOverlappingReservation() {

        Instant start = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant end = start.plus(1, ChronoUnit.HOURS);
        ReservationRequest request = new ReservationRequest(1L, "Ana", start, end);

        Room room = new Room("Sala A", 8, "P1");
        room.setId(1L);
        when(roomService.getRoomOrThrow(1L)).thenReturn(room);
        when(reservationRepository.existsOverlappingReservation(eq(1L), any(), any()))
            .thenReturn(true);

        RoomOccupiedException ex = assertThrows(RoomOccupiedException.class,
            () -> reservationService.createReservation(request));
        assertTrue(ex.getMessage().contains("ya está reservada"));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldCreateReservationWhenNoOverlap() {

        Instant start = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant end = start.plus(1, ChronoUnit.HOURS);
        ReservationRequest request = new ReservationRequest(1L, "Ana", start, end);

        Room room = new Room("Sala A", 8, "P1");
        room.setId(1L);

        Reservation saved = new Reservation(room, "Ana", start, end);
        saved.setId(10L);

        when(roomService.getRoomOrThrow(1L)).thenReturn(room);
        when(reservationRepository.existsOverlappingReservation(eq(1L), any(), any()))
            .thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);
        when(reservationMapper.toResponse(any(Reservation.class)))
            .thenReturn(new ReservationResponse(
                10L, 1L, "Sala A", "Ana", start, end, "ACTIVE", Instant.now()));

        var response = reservationService.createReservation(request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("ACTIVE", response.status());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldCancelReservation() {
        Reservation reservation = new Reservation(
            new Room("Sala A", 8, "P1"), "Ana",
            Instant.now().plus(1, ChronoUnit.HOURS),
            Instant.now().plus(2, ChronoUnit.HOURS));
        reservation.setId(5L);
        reservation.setStatus(ReservationStatus.ACTIVE);

        when(reservationRepository.findById(5L)).thenReturn(java.util.Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        reservationService.cancelReservation(5L);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }
}