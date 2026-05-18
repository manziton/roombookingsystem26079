package com.example.eventbooking.repository;

import com.example.eventbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByRoomId(UUID roomId);
    List<Booking> findByStudentStudentId(String studentId);
    boolean existsByRoomIdAndBookingDate(UUID roomId, String bookingDate);
}
