package com.test.salas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.salas.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

	boolean existsByNameIgnoreCase(String name);
	
}
