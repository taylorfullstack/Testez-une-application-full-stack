package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.openclassrooms.starterjwt.testUtils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("loadUserByUsername - Success")
    void shouldLoadUserByUsername() {
        // Arrange
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setEmail(TEST_USER_EMAIL);
        expectedUser.setFirstName(TEST_USER_FIRST_NAME);
        expectedUser.setLastName(TEST_USER_LAST_NAME);
        expectedUser.setPassword(TEST_USER_PASSWORD);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(expectedUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USER_EMAIL);

        // Assert
        assertEquals(expectedUser.getEmail(), userDetails.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername - Not found exception")
    void shouldThrowUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(TEST_USER_EMAIL));
    }
}