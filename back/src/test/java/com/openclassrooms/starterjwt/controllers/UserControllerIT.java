package com.openclassrooms.starterjwt.controllers;

import static com.openclassrooms.starterjwt.testUtils.TestConstants.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = TEST_SCRIPT)
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Find user by ID - Success")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void findById() throws Exception {
        mockMvc.perform(get("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(TEST_ADMIN_EMAIL));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = 1", Integer.class);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Find user by ID - Not Found")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void findById_NotFound() throws Exception {
        mockMvc.perform(get("/api/user/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = 9999", Integer.class);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Find user by ID - Bad Request")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void findById_BadRequest() throws Exception {
        // Get the total number of users before the operation
        Integer countBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

        mockMvc.perform(get("/api/user/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Integer countAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

        assertEquals(countBefore, countAfter);
    }

    @Test
    @DisplayName("Delete user by ID - Success")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void deleteById() throws Exception {
        mockMvc.perform(delete("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = 1", Integer.class);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Delete user by ID - Not Found")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void deleteById_NotFound() throws Exception {
        mockMvc.perform(delete("/api/user/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = 9999", Integer.class);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Delete user by ID - Bad Request")
    @WithMockUser(username = TEST_ADMIN_EMAIL, roles = TEST_ADMIN_ROLE)
    void deleteById_BadRequest() throws Exception {
        Integer countBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

        mockMvc.perform(delete("/api/user/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Integer countAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

        assertEquals(countBefore, countAfter);
    }

    @Test
    @DisplayName("Delete user by ID - Unauthorized")
    @WithMockUser(username = "other@user.com")
    void deleteById_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE id = 1", Integer.class);
        assertEquals(1, count);
    }
}