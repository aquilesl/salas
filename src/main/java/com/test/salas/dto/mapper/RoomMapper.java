package com.test.salas.dto.mapper;

import org.springframework.stereotype.Component;
import com.test.salas.dto.RoomRequest;
import com.test.salas.dto.RoomResponse;
import com.test.salas.entity.Room;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest request) {
        Room room = new Room();
        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setLocation(request.location());
        return room;
    }

    public RoomResponse toResponse(Room room) {
        return new RoomResponse(
            room.getId(),
            room.getName(),
            room.getCapacity(),
            room.getLocation()
        );
    }
}
