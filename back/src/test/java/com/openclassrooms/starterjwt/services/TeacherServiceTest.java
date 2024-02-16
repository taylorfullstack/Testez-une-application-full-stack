package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class TeacherServiceTest {

    private static final Long EXISTING_TEACHER_ID = 1L;
    private static final Long NON_EXISTING_TEACHER_ID = 3L;

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    private Teacher expectedTeacher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        expectedTeacher = createTeacher(EXISTING_TEACHER_ID, "John");
        Teacher secondTeacher = createTeacher(2L, "Jane");

        when(teacherRepository.findById(EXISTING_TEACHER_ID)).thenReturn(Optional.of(expectedTeacher));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(secondTeacher));
        when(teacherRepository.findById(NON_EXISTING_TEACHER_ID)).thenReturn(Optional.empty());

        List<Teacher> expectedTeachers = Arrays.asList(expectedTeacher, secondTeacher);
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);
    }

    private Teacher createTeacher(Long id, String firstName) {
        return new Teacher(
                id,
                "mockLastName",
                firstName,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("When findAll is called, then return all teachers")
    void shouldReturnAllTeachersWhenFindAllIsCalled() {
        // Act
        List<Teacher> actualTeachers = teacherService.findAll();

        // Assert
        assertEquals(2, actualTeachers.size());
        assertEquals(expectedTeacher, actualTeachers.get(0));
    }

    @Test
    @DisplayName("When findById is called with existing id, then return the teacher")
    void shouldReturnTeacherWhenFindByIdIsCalledWithExistingId() {
        // Act
        Teacher actualTeacher = teacherService.findById(EXISTING_TEACHER_ID);

        // Assert
        assertEquals(expectedTeacher, actualTeacher);
    }

    @Test
    @DisplayName("When findById is called with non-existing id, then return null")
    void shouldReturnNullWhenFindByIdIsCalledWithNonExistingId() {
        // Act
        Teacher actualTeacher = teacherService.findById(NON_EXISTING_TEACHER_ID);

        // Assert
        assertNull(actualTeacher);
    }
}