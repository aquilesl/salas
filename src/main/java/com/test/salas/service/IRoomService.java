package com.test.salas.service;

import com.test.salas.dto.RoomResponse;
import com.test.salas.entity.Room;

import java.util.List;

import com.test.salas.dto.RoomRequest;

public interface IRoomService {

	public RoomResponse createRoom(RoomRequest request);
	public List<RoomResponse> listRooms();
	public Room getRoomOrThrow(Long id);
	
}
