package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.openclassrooms.starterjwt.testUtils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = TEST_SCRIPT)
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_USER_EMAIL);
        loginRequest.setPassword(TEST_USER_PASSWORD);

        signupRequest = new SignupRequest();
        signupRequest.setEmail(TEST_USER_EMAIL);
        signupRequest.setFirstName(TEST_USER_FIRST_NAME);
        signupRequest.setLastName(TEST_TEACHER_LAST_NAME);
        signupRequest.setPassword(TEST_USER_PASSWORD);

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Register User - Email Already Taken")
    public void shouldNotRegisterUser_EmailAlreadyTaken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        SignupRequest anotherSignupRequest = new SignupRequest();
        anotherSignupRequest.setEmail(signupRequest.getEmail());
        anotherSignupRequest.setFirstName("anotherFirstName");
        anotherSignupRequest.setLastName("anotherLastName");
        anotherSignupRequest.setPassword("anotherPassword");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(anotherSignupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    @DisplayName("Login User - Incorrect Credentials")
    public void shouldNotAuthenticateUser_IncorrectCredentials() throws Exception {
        LoginRequest wrongLoginRequest = new LoginRequest();
        wrongLoginRequest.setEmail(signupRequest.getEmail());
        wrongLoginRequest.setPassword("wrong-password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongLoginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Register then Login User - Success")
    public void shouldRegisterThenLoginUser() throws Exception {
        User user = User.builder()
                .email(signupRequest.getEmail())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .password(signupRequest.getPassword())
                .admin(false)
                .build();

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class, signupRequest.getEmail()
        );

        assertEquals(1, count);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        String userString = user.toString();
        assertNotNull(userString);
    }
}