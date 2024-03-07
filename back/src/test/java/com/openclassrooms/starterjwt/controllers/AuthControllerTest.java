package com.openclassrooms.starterjwt.controllers;
import static com.openclassrooms.starterjwt.testUtils.TestConstants.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication auth;
    private LoginRequest loginRequest;
    private SignupRequest signUpRequest;
    private UserDetailsImpl userDetails;

    @BeforeEach
    public void setup() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_USER_EMAIL);
        loginRequest.setPassword(TEST_USER_PASSWORD);

        signUpRequest = new SignupRequest();
        signUpRequest.setEmail(TEST_USER_EMAIL);
        signUpRequest.setFirstName(TEST_USER_FIRST_NAME);
        signUpRequest.setLastName(TEST_USER_LAST_NAME);
        signUpRequest.setPassword(TEST_USER_PASSWORD);

        userDetails = new UserDetailsImpl(1L, TEST_USER_EMAIL, TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME, true, TEST_USER_PASSWORD);
    }

    @Test
    @DisplayName("Authenticate User - Success")
    public void shouldAuthenticateUserAndReturnJwtResponse() {
        // Arrange
        User user = new User(TEST_USER_EMAIL, TEST_USER_LAST_NAME, TEST_USER_FIRST_NAME, TEST_USER_PASSWORD, true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtils.generateJwtToken(auth)).thenReturn(TEST_JWT_TOKEN);
        when(auth.getPrincipal()).thenReturn(userDetails);

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertInstanceOf(JwtResponse.class, response.getBody());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals(TEST_USER_EMAIL, jwtResponse.getUsername());
        assertEquals(TEST_USER_FIRST_NAME, jwtResponse.getFirstName());
        assertEquals(TEST_USER_LAST_NAME, jwtResponse.getLastName());
        assertEquals(TEST_JWT_TOKEN, jwtResponse.getToken());

        // Verify isAdmin
        verify(userRepository).findByEmail(TEST_USER_EMAIL);
        assertTrue(user.isAdmin());

        // Verify interaction with authenticationManager
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(TEST_USER_EMAIL, TEST_USER_PASSWORD));
    }

    @Test
    @DisplayName("Register User - Success")
    public void shouldCreateAndSaveNewUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(TEST_USER_ENCODED_PASSWORD);

        // Act
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(TEST_USER_EMAIL, savedUser.getEmail());
        assertEquals(TEST_USER_FIRST_NAME, savedUser.getFirstName());
        assertEquals(TEST_USER_LAST_NAME, savedUser.getLastName());
        assertEquals(TEST_USER_ENCODED_PASSWORD, savedUser.getPassword());
        assertNotNull(response.getBody());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("User registered successfully!", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    @DisplayName("Register User - Failure (Email Already Taken)")
    public void shouldReturnBadRequestWhenEmailIsTaken() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("Error: Email is already taken!", ((MessageResponse) response.getBody()).getMessage());
    }
}