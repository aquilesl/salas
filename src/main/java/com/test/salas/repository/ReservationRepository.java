package com.test.salas.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.test.salas.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{

	@Query("""
	        SELECT COUNT(r) > 0 FROM Reservation r
	        WHERE r.room.id = :roomId
	          AND r.status = com.test.salas.entity.ReservationStatus.ACTIVE
	          AND r.startTime < :endTime
	          AND r.endTime > :startTime
	        """)
	    boolean existsOverlappingReservation(
	        @Param("roomId") Long roomId,
	        @Param("startTime") Instant startTime,
	        @Param("endTime") Instant endTime
	    );

	    @Query("""
	        SELECT r FROM Reservation r
	        WHERE (:roomId IS NULL OR r.room.id = :roomId)
	          AND (:from IS NULL OR r.startTime >= :from)
	          AND (:to IS NULL OR r.endTime <= :to)
	        """)
	    Page<Reservation> findByFilters(
	        @Param("roomId") Long roomId,
	        @Param("from") Instant from,
	        @Param("to") Instant to,
	        Pageable pageable
	    );
	
}
