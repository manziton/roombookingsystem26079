package com.example.eventbooking.config;

import com.example.eventbooking.model.Room;
import com.example.eventbooking.model.Student;
import com.example.eventbooking.repository.RoomRepository;
import com.example.eventbooking.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public DatabaseInitializer(RoomRepository roomRepository, StudentRepository studentRepository) {
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roomRepository.count() == 0) {
            Room r1 = new Room("ROM104", 6, "Library Ground Floor", true);
            Room r2 = new Room("ROM201", 10, "Science Building 2nd Floor", true);
            Room r3 = new Room("ROM305", 4, "Engineering Block A 3rd Floor", true);
            Room r4 = new Room("ROM102", 8, "Main Campus Block C", true);
            Room r5 = new Room("ROM204", 15, "IT Center Level 2", false); // Not bookable initially for demo

            roomRepository.saveAll(Arrays.asList(r1, r2, r3, r4, r5));
            System.out.println("Database Seeder: Seeded 5 Study Rooms.");
        }

        if (studentRepository.count() == 0) {
            Student s1 = new Student("STD1001", "Hillary Hillary", "hillary@campus.edu", "+1234567890", "Computer Science");
            Student s2 = new Student("STD1002", "Alex Mercer", "alex@campus.edu", "+1234567891", "Information Technology");
            Student s3 = new Student("STD1003", "Sarah Connor", "sarah@campus.edu", "+1234567892", "Mechanical Engineering");

            studentRepository.saveAll(Arrays.asList(s1, s2, s3));
            System.out.println("Database Seeder: Seeded 3 Student Representatives.");
        }
    }
}
