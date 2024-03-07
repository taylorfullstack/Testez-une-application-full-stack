package com.openclassrooms.starterjwt.controllers;

import static com.openclassrooms.starterjwt.testUtils.TestConstants.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = TEST_SCRIPT)
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long teacherId;
    private Long userId;

    private static final String ID_COLUMN = "id";

    @BeforeEach
    public void setup() {
        jdbcTemplate.update(
                "INSERT INTO TEACHERS (last_name, first_name) VALUES (?, ?)",
                TEST_TEACHER_LAST_NAME, TEST_TEACHER_FIRST_NAME
        );

        jdbcTemplate.update(
                "INSERT INTO USERS (last_name, first_name, email, password) VALUES (?, ?, ?, ?)",
                TEST_USER_LAST_NAME, TEST_USER_FIRST_NAME, TEST_USER_EMAIL, TEST_USER_PASSWORD
        );

        teacherId = jdbcTemplate.queryForObject(
                "SELECT id FROM TEACHERS WHERE last_name = ? AND first_name = ?",
                (rs, rowNum) -> rs.getLong(ID_COLUMN),
                TEST_TEACHER_LAST_NAME, TEST_TEACHER_FIRST_NAME
        );

        userId = jdbcTemplate.queryForObject(
                "SELECT id FROM USERS WHERE email = ?",
                (rs, rowNum) -> rs.getLong(ID_COLUMN),
                TEST_USER_EMAIL
        );

        jdbcTemplate.update(
                "INSERT INTO SESSIONS (name, description, date, teacher_id) VALUES (?, ?, ?, ?)",
                TEST_SESSION_NAME, TEST_SESSION_DESCRIPTION, new Timestamp(System.currentTimeMillis()), teacherId
        );
    }

    @Test
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = {TEST_ADMIN_ROLE})
    public void shouldCreateSession() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName(TEST_SESSION_NAME);
        sessionDto.setTeacher_id(teacherId);

        sessionDto.setDescription(TEST_SESSION_DESCRIPTION);
        sessionDto.setDate(new Timestamp(System.currentTimeMillis()));

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_SESSION_NAME))
                .andExpect(jsonPath("$.description").value(TEST_SESSION_DESCRIPTION))
                .andExpect(jsonPath("$.teacher_id").value(teacherId));

    }

    @Test
    @WithMockUser(username = TEST_USER_EMAIL)
    public void shouldParticipateInSession() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = TEST_USER_EMAIL)
    public void shouldNoLongerParticipateInSession() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/session/1/participate/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}