package com.test.salas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.test.salas.entity.Room;
import com.test.salas.repository.ReservationRepository;
import com.test.salas.repository.RoomRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Room testRoom;

    @BeforeEach
    void setUp() {
    	
    	Room room = new Room("Sala Test " + UUID.randomUUID(), 8, "P1");
    	
        testRoom = roomRepository.save( room );
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    @DisplayName("[Integración] POST /api/v1/reservations devuelve 201, 409 y 400 según el caso")
    void postReservation_fullHttpFlow() throws Exception {

        Instant start = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        String validBody = """
                {
                  "roomId": %d,
                  "responsible": "Ana Pérez",
                  "startTime": "%s",
                  "endTime": "%s"
                }
                """.formatted(testRoom.getId(), start, end);

        // ---- Caso 1: reserva válida -> 201 Created ----
        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.responsible").value("Ana Pérez"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Fuerza el flush para que el siguiente SELECT vea la reserva recién creada
        reservationRepository.flush();

        // ---- Caso 2: mismo cuerpo otra vez -> 409 Conflict ----
        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isConflict());

        // ---- Caso 3: cuerpo inválido (sin roomId y responsable vacío) -> 400 Bad Request ----
        String invalidBody = """
                {
                  "responsible": "",
                  "startTime": "%s",
                  "endTime": "%s"
                }
                """.formatted(start, end);

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.responsible").exists());
    }
}