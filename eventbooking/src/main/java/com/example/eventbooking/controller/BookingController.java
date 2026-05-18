package com.example.eventbooking.controller;

import com.example.eventbooking.model.Booking;
import com.example.eventbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Booking> bookRoom(
            @PathVariable("id") UUID roomId,
            @RequestBody BookingRequest request) {
        Booking booking = bookingService.bookRoom(
                roomId,
                request.studentName(),
                request.studentId(),
                request.bookingDate()
        );
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable("id") UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable("bookingId") UUID bookingId,
            @RequestBody Booking bookingDetails) {
        Booking updatedBooking = bookingService.updateBooking(bookingId, bookingDetails);
        return ResponseEntity.ok(updatedBooking);
    }

    // Modern DTO Request representation using Java Records
    public record BookingRequest(String studentName, String studentId, String bookingDate) {}
}
