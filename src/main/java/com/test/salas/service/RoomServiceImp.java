package com.test.salas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.salas.dto.RoomResponse;
import com.test.salas.Exceptions.ResourceNotFoundException;
import com.test.salas.dto.RoomRequest;
import com.test.salas.dto.mapper.RoomMapper;
import com.test.salas.entity.Room;
import com.test.salas.repository.RoomRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImp implements IRoomService{

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImp(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Ya existe una sala con ese nombre");
        }
        Room room = roomMapper.toEntity(request);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listRooms() {
        return roomRepository.findAll().stream()
            .map(roomMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Room getRoomOrThrow(Long id) {
        return roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + id));
    }
}
