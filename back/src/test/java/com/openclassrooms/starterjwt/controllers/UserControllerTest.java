package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    private User user1;
    private UserDto userDto1;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);

        userDto1 = new UserDto();
        userDto1.setId(1L);
    }

    @Nested
    @DisplayName("Find by ID tests")
    class FindByIdTests {

        @Test
        @DisplayName("Success")
        void shouldFindById() {
            // Arrange
            when(userService.findById(1L)).thenReturn(user1);
            when(userMapper.toDto(user1)).thenReturn(userDto1);

            // Act
            ResponseEntity<?> response = userController.findById("1");

            // Assert
            assertEquals(200, response.getStatusCodeValue());
            assertInstanceOf(UserDto.class, response.getBody());
            assertEquals(userDto1, response.getBody());
            verify(userService, times(1)).findById(1L);
            verify(userMapper, times(1)).toDto(user1);
        }

        @Test
        @DisplayName("Bad Request")
        void shouldNotFindByIdWithInvalidId() {
            // Act
            ResponseEntity<?> response = userController.findById("invalid");

            // Assert
            assertEquals(400, response.getStatusCodeValue());
        }

        @Test
        @DisplayName("Not Found")
        void shouldReturnNotFoundForFindById() {
            // Arrange
            when(userService.findById(anyLong())).thenReturn(null);

            // Act
            ResponseEntity<?> response = userController.findById("1");

            // Assert
            assertEquals(404, response.getStatusCodeValue());
        }
    }

    @Nested
    @DisplayName("Delete tests")
    class DeleteTests {

        @Test
        @DisplayName("Success with Authorized User")
        void shouldDeleteWithAuthorizedUser() {
            // Arrange
            when(userService.findById(anyLong())).thenReturn(user1);

            // Mock the SecurityContextHolder
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn(user1.getEmail());
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(new TestingAuthenticationToken(userDetails, null));
            SecurityContextHolder.setContext(securityContext);

            // Act
            ResponseEntity<?> response = userController.save("1");

            // Assert
            assertEquals(200, response.getStatusCodeValue());
        }

        @Test
        @DisplayName("Bad Request with Invalid ID")
        void shouldNotDeleteWithInvalidId() {
            // Act
            ResponseEntity<?> response = userController.save("invalid");

            // Assert
            assertEquals(400, response.getStatusCodeValue());
        }

        @Test
        @DisplayName("Not Found User")
        void shouldNotDeleteWithNotFoundUser() {
            // Arrange
            when(userService.findById(anyLong())).thenReturn(null);

            // Act
            ResponseEntity<?> response = userController.save("1");

            // Assert
            assertEquals(404, response.getStatusCodeValue());
        }

        @Test
        @DisplayName("Unauthorized User")
        void shouldNotDeleteWithUnauthorizedUser() {
            // Arrange
            when(userService.findById(anyLong())).thenReturn(user1);

            // Mock the SecurityContextHolder
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("unauthorized@example.com");
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(new TestingAuthenticationToken(userDetails, null));
            SecurityContextHolder.setContext(securityContext);

            // Act
            ResponseEntity<?> response = userController.save("1");

            // Assert
            assertEquals(401, response.getStatusCodeValue());
        }
    }
}