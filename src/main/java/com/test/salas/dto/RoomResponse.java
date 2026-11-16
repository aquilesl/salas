package com.test.salas.dto;

public record RoomResponse(
	    Long id,
	    String name,
	    Integer capacity,
	    String location
	) {}