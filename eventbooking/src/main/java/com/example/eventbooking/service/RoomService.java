package com.example.eventbooking.service;

import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.model.Room;
import com.example.eventbooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    @Transactional
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(UUID id, Room roomDetails) {
        Room room = getRoomById(id);
        room.setName(roomDetails.getName());
        room.setCapacity(roomDetails.getCapacity());
        room.setLocation(roomDetails.getLocation());
        room.setAvailable(roomDetails.getAvailable());
        return roomRepository.save(room);
    }
}
