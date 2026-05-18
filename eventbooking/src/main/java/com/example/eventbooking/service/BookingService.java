package com.example.eventbooking.service;

import com.example.eventbooking.exception.ResourceAlreadyExistsException;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.model.Booking;
import com.example.eventbooking.model.Room;
import com.example.eventbooking.model.Student;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.RoomRepository;
import com.example.eventbooking.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, 
                          RoomRepository roomRepository, 
                          StudentRepository studentRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    public List<Booking> getBookingsByRoomId(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }
        return bookingRepository.findByRoomId(roomId);
    }

    @Transactional
    public Booking bookRoom(UUID roomId, String studentName, String studentId, String bookingDate) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        if (!room.getAvailable()) {
            throw new IllegalStateException("Room '" + room.getName() + "' is currently not bookable.");
        }

        // Check if there is already a booking for this room on the same date
        if (bookingRepository.existsByRoomIdAndBookingDate(roomId, bookingDate)) {
            throw new ResourceAlreadyExistsException(
                    "Room '" + room.getName() + "' is already booked for " + bookingDate + "."
            );
        }

        // Find existing student or register a new student representative
        Student student = studentRepository.findByStudentId(studentId)
                .map(s -> {
                    // Update name if changed
                    if (!s.getName().equalsIgnoreCase(studentName)) {
                        s.setName(studentName);
                        return studentRepository.save(s);
                    }
                    return s;
                })
                .orElseGet(() -> {
                    Student newStudent = new Student();
                    newStudent.setStudentId(studentId);
                    newStudent.setName(studentName);
                    // generate unique, dummy institutional attributes
                    newStudent.setEmail(studentId.toLowerCase() + "@example.com");
                    newStudent.setPhone("N/A");
                    newStudent.setDepartment("N/A");
                    return studentRepository.save(newStudent);
                });

        Booking booking = new Booking(student, room, bookingDate);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(UUID id) {
        Booking booking = getBookingById(id);
        bookingRepository.delete(booking);
    }

    @Transactional
    public Booking updateBooking(UUID bookingId, Booking bookingDetails) {
        Booking booking = getBookingById(bookingId);
        
        String detailsStudentId = bookingDetails.getStudentId();
        String detailsStudentName = bookingDetails.getStudentName();
        
        // Find existing student or register a new student representative
        Student student = studentRepository.findByStudentId(detailsStudentId)
                .map(s -> {
                    s.setName(detailsStudentName);
                    return studentRepository.save(s);
                })
                .orElseGet(() -> {
                    Student newStudent = new Student();
                    newStudent.setStudentId(detailsStudentId);
                    newStudent.setName(detailsStudentName);
                    newStudent.setEmail(detailsStudentId.toLowerCase() + "@example.com");
                    newStudent.setPhone("N/A");
                    newStudent.setDepartment("N/A");
                    return studentRepository.save(newStudent);
                });
                
        booking.setStudent(student);
        booking.setBookingDate(bookingDetails.getBookingDate());
        booking.setReleased(bookingDetails.getReleased());
        
        // If updating the room is needed, handle room update:
        if (bookingDetails.getRoom() != null && !bookingDetails.getRoom().getId().equals(booking.getRoom().getId())) {
            Room newRoom = roomRepository.findById(bookingDetails.getRoom().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("New Room not found with id: " + bookingDetails.getRoom().getId()));
            booking.setRoom(newRoom);
        }
        
        return bookingRepository.save(booking);
    }
}
