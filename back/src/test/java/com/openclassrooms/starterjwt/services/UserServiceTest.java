package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final Long EXISTING_USER_ID = 1L;
    private static final Long NON_EXISTING_USER_ID = 2L;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User expectedUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        expectedUser = createUser(EXISTING_USER_ID);

        when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(expectedUser));
        when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());
    }

    private User createUser(Long id) {
        return new User(
                id,
                "user@test.com",
                "Doe",
                "John",
                "123456",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("When delete is called, then delete user by id")
    void shouldDeleteUserWhenDeleteIsCalled() {
        // Act
        userService.delete(EXISTING_USER_ID);

        // Assert
        verify(userRepository).deleteById(EXISTING_USER_ID);
    }

    @Test
    @DisplayName("When findById is called with existing id, then return the user")
    void shouldReturnUserWhenFindByIdIsCalledWithExistingId() {
        // Act
        User actualUser = userService.findById(EXISTING_USER_ID);

        // Assert
        assertEquals(expectedUser, actualUser);
    }

    @Test
    @DisplayName("When findById is called with non-existing id, then return null")
    void shouldReturnNullWhenFindByIdIsCalledWithNonExistingId() {
        // Act
        User actualUser = userService.findById(NON_EXISTING_USER_ID);

        // Assert
        assertNull(actualUser);
    }
}
