package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    @InjectMocks
    private TeacherController teacherController;
    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;
    private Teacher teacher1;
    private Teacher teacher2;
    private TeacherDto teacherDto1;
    private TeacherDto teacherDto2;


    @BeforeEach
    void setUp() {
        teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher2 = new Teacher();
        teacher2.setId(2L);

        teacherDto1 = new TeacherDto();
        teacherDto1.setId(1L);
        teacherDto2 = new TeacherDto();
        teacherDto2.setId(2L);
    }

    @Test
    @DisplayName("Find by ID - Success")
    void shouldFindById() {
        // Arrange
        when(teacherService.findById(1L)).thenReturn(teacher1);
        when(teacherMapper.toDto(teacher1)).thenReturn(teacherDto1);

        // Act
        ResponseEntity<?> response = teacherController.findById("1");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(TeacherDto.class, response.getBody());
        assertEquals(teacherDto1, response.getBody());
        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, times(1)).toDto(teacher1);
    }

    @Test
    @DisplayName("Find All - Success")
    void shouldFindAll() {
        // Arrange
        when(teacherService.findAll()).thenReturn(Arrays.asList(teacher1, teacher2));
        when(teacherMapper.toDto(Arrays.asList(teacher1, teacher2))).thenReturn(Arrays.asList(teacherDto1, teacherDto2));

        // Act
        ResponseEntity<?> response = teacherController.findAll();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(List.class, response.getBody());
        List<?> bodyList = (List<?>) response.getBody();
        assertFalse(bodyList.isEmpty());

        List<TeacherDto> teacherDtos = new ArrayList<>();
        for (Object obj : bodyList) {
            if (obj instanceof TeacherDto) {
                teacherDtos.add((TeacherDto) obj);
            }
        }

        assertEquals(2, teacherDtos.size());
        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(Arrays.asList(teacher1, teacher2));
    }

    @Test
    @DisplayName("Find by ID - Bad Request")
    void shouldThrowNumberFormatExceptionForFindById() {
        // Act
        ResponseEntity<?> response = teacherController.findById("invalid");

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Find by ID - Not Found")
    void shouldReturnNotFoundForFindById() {
        // Arrange
        when(teacherService.findById(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<?> response = teacherController.findById("1");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }
}