package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.openclassrooms.starterjwt.testUtils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails1;
    private UserDetailsImpl userDetails2;

    @BeforeEach
    void setUp() {
        userDetails1 = UserDetailsImpl.builder()
                .id(1L)
                .username(TEST_USER_EMAIL)
                .firstName(TEST_USER_FIRST_NAME)
                .lastName(TEST_USER_LAST_NAME)
                .admin(false)
                .password(TEST_USER_PASSWORD)
                .build();

        userDetails2 = UserDetailsImpl.builder()
                .id(2L)
                .username("anotherEmail@example.com")
                .firstName("Another")
                .lastName("User")
                .admin(true)
                .password("anotherPassword")
                .build();
    }

    @Test
    @DisplayName("UserDetailsImpl Creation - Success")
    void shouldCreateUserDetailsImpl() {
        // Arrange
        UserDetailsImpl newUserDetails = UserDetailsImpl.builder()
                .id(1L)
                .username(TEST_USER_EMAIL)
                .firstName(TEST_USER_FIRST_NAME)
                .lastName(TEST_USER_LAST_NAME)
                .admin(false)
                .password(TEST_USER_PASSWORD)
                .build();

        // Act
        Long id = newUserDetails.getId();
        String username = newUserDetails.getUsername();
        String firstName = newUserDetails.getFirstName();
        String lastName = newUserDetails.getLastName();
        String password = newUserDetails.getPassword();
        boolean admin = newUserDetails.getAdmin();

        // Assert
        assertEquals(1L, id);
        assertEquals(TEST_USER_EMAIL, username);
        assertEquals(TEST_USER_FIRST_NAME, firstName);
        assertEquals(TEST_USER_LAST_NAME, lastName);
        assertEquals(TEST_USER_PASSWORD, password);
        assertFalse(admin);
    }

    @Test
    @DisplayName("Equals - Same values")
    void shouldReturnTrueWhenComparingDifferentInstancesWithSameValues() {
        // Arrange
        UserDetailsImpl userDetails3 = UserDetailsImpl.builder()
                .id(1L)
                .username(TEST_USER_EMAIL)
                .firstName(TEST_USER_FIRST_NAME)
                .lastName(TEST_USER_LAST_NAME)
                .admin(false)
                .password(TEST_USER_PASSWORD)
                .build();

        // Act
        boolean isEqual = userDetails1.equals(userDetails3);

        // Assert
        assertTrue(isEqual);
    }

    @Test
    @DisplayName("Equals - Same Instance")
    void shouldReturnTrueWhenComparingSameInstance() {
        // Act
        boolean isEqual = userDetails1.equals(userDetails1);

        // Assert
        assertTrue(isEqual);
    }

    @Test
    @DisplayName("Equals - Different values")
    void shouldReturnFalseWhenComparingDifferentInstances() {
        // Act
        boolean isEqual = userDetails1.equals(userDetails2);

        // Assert
        assertFalse(isEqual);
    }

    @Test
    @DisplayName("Equals - Different Class")
    void shouldReturnFalseWhenComparingWithDifferentClass() {
        // Arrange
        Object object = new Object();

        // Act
        boolean isEqual = userDetails1.equals(object);

        // Assert
        assertFalse(isEqual);
    }

    @Test
    @DisplayName("GetAuthorities - Returns Empty Set")
    void shouldReturnEmptySetForGetAuthorities() {
        // Act & Assert
        assertTrue(userDetails1.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("IsAccountNonExpired - Returns True")
    void shouldReturnTrueForIsAccountNonExpired() {
        // Act & Assert
        assertTrue(userDetails1.isAccountNonExpired());
    }

    @Test
    @DisplayName("IsAccountNonLocked - Returns True")
    void shouldReturnTrueForIsAccountNonLocked() {
        // Act & Assert
        assertTrue(userDetails1.isAccountNonLocked());
    }

    @Test
    @DisplayName("IsCredentialsNonExpired - Returns True")
    void shouldReturnTrueForIsCredentialsNonExpired() {
        // Act & Assert
        assertTrue(userDetails1.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("IsEnabled - Returns True")
    void shouldReturnTrueForIsEnabled() {
        // Act & Assert
        assertTrue(userDetails1.isEnabled());
    }
}
