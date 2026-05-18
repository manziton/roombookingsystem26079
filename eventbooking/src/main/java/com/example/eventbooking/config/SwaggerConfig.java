package com.example.eventbooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Campus Study Room Booking API")
                        .version("1.0.0")
                        .description("""
                                REST API for the **Campus Study Room Booking System** — a room reservation system for student class representatives.
                                
                                ## Features
                                - 🏫 **Room Management** — Register study rooms, define capacities and floor locations
                                - 📅 **Room Bookings** — Book study rooms on specific dates with student representative credentials
                                - 🔄 **Booking Modifications & Releases** — Update or cancel reservations to release rooms for other students
                                
                                ## Business Rules
                                - A room **cannot be booked twice on the same date** (conflict resolution)
                                - Room bookings are only allowed on **available** rooms
                                - Deleting a room or booking is handled cleanly via the repository cascades
                                """)
                        .contact(new Contact()
                                .name("Study Room Support")
                                .email("rooms@university.edu")
                                .url("http://localhost:5173"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ))
                .tags(List.of(
                        new Tag().name("Rooms").description("CRUD operations for study room records"),
                        new Tag().name("Bookings").description("Room reservation — book, update, and release campus study rooms")
                ));
    }
}
