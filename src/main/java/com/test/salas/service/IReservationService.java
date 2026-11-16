package com.test.salas.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.test.salas.dto.ReservationRequest;
import com.test.salas.dto.ReservationResponse;

public interface IReservationService {

	public ReservationResponse createReservation(ReservationRequest request);
	public void cancelReservation(Long id);
	public Page<ReservationResponse> listReservations(
            Long roomId, Instant from, Instant to, Pageable pageable);
	
}
