package com.test.salasControllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.salas.dto.RoomResponse;
import com.test.salas.dto.RoomRequest;
import com.test.salas.service.IRoomService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final IRoomService roomService;

    public RoomController(IRoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse created = roomService.createRoom(request);
        return ResponseEntity
            .created(URI.create("/api/v1/rooms/" + created.id()))
            .body(created);
    }

    @GetMapping
    public List<RoomResponse> listRooms() {
        return roomService.listRooms();
    }
}
