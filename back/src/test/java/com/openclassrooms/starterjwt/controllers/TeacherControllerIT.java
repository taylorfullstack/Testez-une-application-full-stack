package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.openclassrooms.starterjwt.testUtils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = TEST_SCRIPT)
public class TeacherControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Find teacher by ID - Success")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void findById() throws Exception {
        mockMvc.perform(get("/api/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers WHERE id = 1", Integer.class);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Find teacher by ID - Not Found")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void findById_NotFound() throws Exception {
        mockMvc.perform(get("/api/teacher/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers WHERE id = 9999", Integer.class);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Find teacher by ID - Bad Request")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void findById_BadRequest() throws Exception {
        Integer countBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class);

        mockMvc.perform(get("/api/teacher/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Integer countAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class);

        assertEquals(countBefore, countAfter);
    }

    @Test
    @DisplayName("Find all teachers - Success")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class);

        String jsonResponse = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Teacher> actualTeachers = mapper.readValue(jsonResponse, new TypeReference<List<Teacher>>(){});

        assertEquals(count, actualTeachers.size());
    }
}
