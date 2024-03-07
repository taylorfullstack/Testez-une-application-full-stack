package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SessionControllerTest {
    @InjectMocks
    private SessionController sessionController;

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    private Session session1;
    private Session session2;
    private SessionDto sessionDto1;
    private SessionDto sessionDto2;

    @Mock
    private Teacher teacher1, teacher2;

    @Mock
    private User user1, user2, user3, user4;

    @BeforeEach
    void setUp() {
        session1 = Session.builder()
                .id(1L)
                .name("Session 1")
                .date(new Date())
                .description("Description 1")
                .teacher(teacher1)
                .users(Arrays.asList(user1, user2))
                .build();

        session2 = Session.builder()
                .id(2L)
                .name("Session 2")
                .date(new Date())
                .description("Description 2")
                .teacher(teacher2)
                .users(Arrays.asList(user3, user4))
                .build();

        sessionDto1 = new SessionDto();
        sessionDto1.setId(1L);
        sessionDto1.setName("Session 1");
        sessionDto1.setDate(new Date());
        sessionDto1.setDescription("Description 1");
        sessionDto1.setTeacher_id(1L);
        sessionDto1.setUsers(Arrays.asList(1L, 2L));

        sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setName("Session 2");
        sessionDto2.setDate(new Date());
        sessionDto2.setDescription("Description 2");
        sessionDto2.setTeacher_id(2L);
        sessionDto2.setUsers(Arrays.asList(3L, 4L));
    }

    @Test
    @DisplayName("Find by ID - Success")
    void shouldFindById() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session1);
        when(sessionMapper.toDto(session1)).thenReturn(sessionDto1);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(SessionDto.class, response.getBody());
        assertEquals(sessionDto1, response.getBody());
        verify(sessionService, times(1)).getById(1L);
        verify(sessionMapper, times(1)).toDto(session1);
    }

    @Test
    @DisplayName("Find All - Success")
    void shouldFindAll() {
        // Arrange
        when(sessionService.findAll()).thenReturn(Arrays.asList(session1, session2));
        when(sessionMapper.toDto(Arrays.asList(session1, session2))).thenReturn(Arrays.asList(sessionDto1, sessionDto2));

        // Act
        ResponseEntity<?> response = sessionController.findAll();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(List.class, response.getBody());
        List<?> bodyList = (List<?>) response.getBody();
        assertFalse(bodyList.isEmpty());

        List<SessionDto> sessionDtos = new ArrayList<>();
        for (Object obj : bodyList) {
            if (obj instanceof SessionDto) {
                sessionDtos.add((SessionDto) obj);
            }
        }

        assertEquals(2, sessionDtos.size());
        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(Arrays.asList(session1, session2));
    }

    @Test
    @DisplayName("Create - Success")
    void shouldCreate() {
        // Arrange
        when(sessionMapper.toEntity(sessionDto1)).thenReturn(session1);
        when(sessionService.create(session1)).thenReturn(session1);
        when(sessionMapper.toDto(session1)).thenReturn(sessionDto1);

        // Act
        ResponseEntity<?> response = sessionController.create(sessionDto1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(SessionDto.class, response.getBody());
        assertEquals(sessionDto1, response.getBody());
        verify(sessionService, times(1)).create(session1);
    }

    @Test
    @DisplayName("Update - Success")
    void shouldUpdate() {
        // Arrange
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session1);
        when(sessionService.update(anyLong(), any(Session.class))).thenReturn(session1);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto1);

        // Act
        ResponseEntity<?> response = sessionController.update("1", sessionDto1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(SessionDto.class, response.getBody());
        assertEquals(sessionDto1, response.getBody());
        verify(sessionService, times(1)).update(anyLong(), any(Session.class));
    }

    @Test
    @DisplayName("Delete - Success")
    void shouldDelete() {
        // Arrange
        when(sessionService.getById(anyLong())).thenReturn(session1);

        // Act
        ResponseEntity<?> response = sessionController.save("1");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(sessionService, times(1)).delete(anyLong());
    }

    @Test
    @DisplayName("Participate - Success")
    void shouldParticipate() {
        // Act
        ResponseEntity<?> response = sessionController.participate("1", "1");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(sessionService, times(1)).participate(1L, 1L);
    }

    @Test
    @DisplayName("No Longer Participate - Success")
    void shouldNoLongerParticipate() {
        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "1");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(sessionService, times(1)).noLongerParticipate(1L, 1L);
    }

    @Test
    @DisplayName("Find by ID - Bad Request")
    void shouldThrowNumberFormatExceptionForFindById() {
        // No Arrange needed for this test

        // Act
        ResponseEntity<?> response = sessionController.findById("invalid");

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Update - Bad Request")
    void shouldThrowNumberFormatExceptionForUpdate() {
        // Act
        ResponseEntity<?> response = sessionController.update("invalid", sessionDto1);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Delete - Bad Request")
    void shouldThrowNumberFormatExceptionForDelete() {
        // Act
        ResponseEntity<?> response = sessionController.save("invalid");

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Participate - Bad Request")
    void shouldThrowNumberFormatExceptionForParticipate() {
        // Act
        ResponseEntity<?> response = sessionController.participate("invalid", "1");

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("No Longer Participate - Bad Request")
    void shouldThrowNumberFormatExceptionForNoLongerParticipate() {
        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate("invalid", "1");

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Find by ID - Not Found")
    void shouldReturnNotFoundForFindById() {
        // Arrange
        when(sessionService.getById(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Delete - Not Found")
    void shouldReturnNotFoundForDelete() {
        // Arrange
        when(sessionService.getById(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<?> response = sessionController.save("1");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }
}