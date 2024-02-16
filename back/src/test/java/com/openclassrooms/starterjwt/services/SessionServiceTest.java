package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionServiceTest {

    private static final Long EXISTING_SESSION_ID = 1L;
    private static final Long NON_EXISTING_SESSION_ID = 2L;
    private static final Long EXISTING_USER_ID = 1L;
    private static final Long NON_EXISTING_USER_ID = 2L;

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    private Session expectedSession;
    private User expectedUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        expectedSession = new Session();
        expectedSession.setId(EXISTING_SESSION_ID);
        expectedUser = new User();
        expectedUser.setId(EXISTING_USER_ID);
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(expectedSession));
        when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(expectedUser));
    }

    @Test
    @DisplayName("When create is called, then create session")
    void shouldCreateSessionWhenCreateIsCalled() {
        // Arrange
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session actualSession = sessionService.create(expectedSession);

        // Assert
        verify(sessionRepository).save(expectedSession);
        assertEquals(expectedSession, actualSession);
    }

    @Test
    @DisplayName("When delete is called, then delete session by id")
    void shouldDeleteSessionWhenDeleteIsCalled() {
        // Act
        sessionService.delete(EXISTING_SESSION_ID);

        // Assert
        verify(sessionRepository).deleteById(EXISTING_SESSION_ID);
    }

    @Test
    @DisplayName("When findAll is called, then return all sessions")
    void shouldReturnAllSessionsWhenFindAllIsCalled() {
        // Arrange
        Session secondExpectedSession = new Session();
        secondExpectedSession.setId(2L);
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(expectedSession, secondExpectedSession));

        // Act
        List<Session> sessions = sessionService.findAll();

        // Assert
        assertEquals(2, sessions.size());
        assertTrue(sessions.containsAll(Arrays.asList(expectedSession, secondExpectedSession)));
    }

    @Test
    @DisplayName("When getById is called with existing id, then return the session")
    void shouldReturnSessionWhenGetByIdIsCalledWithExistingId() {
        // Act
        Session actualSession = sessionService.getById(EXISTING_SESSION_ID);

        // Assert
        assertEquals(expectedSession, actualSession);
    }

    @Test
    @DisplayName("When update is called, then update session")
    void shouldUpdateSessionWhenUpdateIsCalled() {
        // Arrange
        Session updatedSession = new Session();
        updatedSession.setId(EXISTING_SESSION_ID);
        when(sessionRepository.save(updatedSession)).thenReturn(updatedSession);

        // Act
        Session actualSession = sessionService.update(EXISTING_SESSION_ID, updatedSession);

        // Assert
        verify(sessionRepository).save(updatedSession);
        assertEquals(updatedSession, actualSession);
    }

    @Test
    @DisplayName("When participate is called with existing session and user id, then add user to session")
    void shouldAddUserToSessionWhenParticipateIsCalledWithExistingSessionAndUserId() {
        // Arrange
        expectedSession.setUsers(new ArrayList<>());
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(expectedSession));
        when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(expectedUser));

        // Act
        sessionService.participate(EXISTING_SESSION_ID, EXISTING_USER_ID);

        // Assert
        verify(sessionRepository).findById(EXISTING_SESSION_ID);
        verify(userRepository).findById(EXISTING_USER_ID);
        assertTrue(expectedSession.getUsers().contains(expectedUser));
        verify(sessionRepository).save(expectedSession);
    }

    @Test
    @DisplayName("When noLongerParticipate is called with existing session and user id, then remove user from session")
    void shouldRemoveUserFromSessionWhenNoLongerParticipateIsCalledWithExistingSessionAndUserId() {
        // Arrange
        User secondExpectedUser = new User();
        secondExpectedUser.setId(2L);
        expectedSession.setUsers(Arrays.asList(expectedUser, secondExpectedUser));
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(expectedSession));

        // Act
        sessionService.noLongerParticipate(EXISTING_SESSION_ID, EXISTING_USER_ID);

        // Assert
        verify(sessionRepository).findById(EXISTING_SESSION_ID);
        assertEquals(1, expectedSession.getUsers().size());
        assertTrue(expectedSession.getUsers().contains(secondExpectedUser));
        verify(sessionRepository).save(expectedSession);
    }

    @Test
    @DisplayName("When participate is called with non-existing session id, then throw NotFoundException")
    void shouldThrowNotFoundExceptionWhenParticipateIsCalledWithNonExistingSessionId() {
        // Arrange
        when(sessionRepository.findById(NON_EXISTING_SESSION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(NON_EXISTING_SESSION_ID, EXISTING_USER_ID));
    }

    @Test
    @DisplayName("When participate is called with existing session id and non-existing user id, then throw NotFoundException")
    void shouldThrowNotFoundExceptionWhenParticipateIsCalledWithExistingSessionIdAndNonExistingUserId() {
        // Arrange
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(expectedSession));
        when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(EXISTING_SESSION_ID, NON_EXISTING_USER_ID));
    }

    @Test
    @DisplayName("When noLongerParticipate is called with non-existing session id, then throw NotFoundException")
    void shouldThrowNotFoundExceptionWhenNoLongerParticipateIsCalledWithNonExistingSessionId() {
        // Arrange
        when(sessionRepository.findById(NON_EXISTING_SESSION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(NON_EXISTING_SESSION_ID, EXISTING_USER_ID));
    }

    @Test
    @DisplayName("When participate is called with existing session id and user already in session, then throw BadRequestException")
    void shouldThrowBadRequestExceptionWhenParticipateIsCalledWithExistingSessionIdAndUserAlreadyInSession() {
        // Arrange
        User secondExpectedUser = new User();
        secondExpectedUser.setId(2L);
        expectedSession.setUsers(Arrays.asList(expectedUser, secondExpectedUser));
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(expectedSession));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> sessionService.participate(EXISTING_SESSION_ID, EXISTING_USER_ID));
    }

    @Test
    @DisplayName("When noLongerParticipate is called with existing session id and user not in session, then throw BadRequestException")
    void shouldThrowBadRequestExceptionWhenNoLongerParticipateIsCalledWithExistingSessionIdAndUserNotInSession() {
        // Arrange
        expectedSession.setUsers(new ArrayList<>());
        when(sessionRepository.findById(EXISTING_SESSION_ID)).thenReturn(Optional.of(expectedSession));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(EXISTING_SESSION_ID, EXISTING_USER_ID));
    }

}