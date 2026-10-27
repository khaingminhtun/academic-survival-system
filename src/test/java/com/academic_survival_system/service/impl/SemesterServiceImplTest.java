
package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.SemesterRequest;
import com.academic_survival_system.dto.response.SemesterResponse;
import com.academic_survival_system.exception.ResourceNotFoundException;
import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Student;
import com.academic_survival_system.repository.SemesterRepository;
import com.academic_survival_system.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemesterServiceImplTest {

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private SemesterServiceImpl semesterService;

    private Student student;
    private Semester semester;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);

        semester = Semester.builder()
                .id(10L)
                .student(student)
                .yearLevel(2)
                .semesterNumber(1)
                .build();
    }

    // -----------------------------------------------
    // 1. CREATE SEMESTER
    // -----------------------------------------------

    @Test
    void shouldCreateSemesterWhenStudentExists() {

        // ARRANGE
        SemesterRequest request = new SemesterRequest(2, 1);

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(semesterRepository.save(any(Semester.class)))
                .thenAnswer(invocation -> {
                    Semester saved = invocation.getArgument(0);
                    saved.setId(10L);
                    return saved;
                });

        // ACT
        SemesterResponse response =
                semesterService.createSemester(1L, request);

        // ASSERT
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(1L, response.studentId());
        assertEquals(2, response.yearLevel());
        assertEquals(1, response.semesterNumber());

        // VERIFY
        verify(studentRepository).findById(1L);
        verify(semesterRepository).save(any(Semester.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingSemesterForMissingStudent() {

        // ARRANGE
        SemesterRequest request = new SemesterRequest(2, 1);

        when(studentRepository.findById(99L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> semesterService.createSemester(99L, request)
        );

        // VERIFY
        verify(studentRepository).findById(99L);
        verifyNoInteractions(semesterRepository);
    }

    // -----------------------------------------------
    // 2. GET SEMESTERS
    // -----------------------------------------------

    @Test
    void shouldReturnAllSemestersWhenYearLevelIsNull() {

        // ARRANGE
        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository
                .findByStudentIdOrderByYearLevelAscSemesterNumberAsc(1L))
                .thenReturn(List.of(semester));

        // ACT
        List<SemesterResponse> responses =
                semesterService.getSemesters(1L, null);

        // ASSERT
        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).id());
        assertEquals(2, responses.get(0).yearLevel());
        assertEquals(1, responses.get(0).semesterNumber());

        // VERIFY
        verify(studentRepository).existsById(1L);

        verify(semesterRepository)
                .findByStudentIdOrderByYearLevelAscSemesterNumberAsc(1L);

        verify(semesterRepository, never())
                .findByStudentIdAndYearLevelOrderBySemesterNumberAsc(
                        anyLong(), anyInt()
                );
    }

    @Test
    void shouldReturnSemestersFilteredByYearLevel() {

        // ARRANGE
        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository
                .findByStudentIdAndYearLevelOrderBySemesterNumberAsc(1L, 2))
                .thenReturn(List.of(semester));

        // ACT
        List<SemesterResponse> responses =
                semesterService.getSemesters(1L, 2);

        // ASSERT
        assertEquals(1, responses.size());
        assertEquals(2, responses.get(0).yearLevel());

        // VERIFY
        verify(semesterRepository)
                .findByStudentIdAndYearLevelOrderBySemesterNumberAsc(1L, 2);

        verify(semesterRepository, never())
                .findByStudentIdOrderByYearLevelAscSemesterNumberAsc(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingSemestersForMissingStudent() {

        // ARRANGE
        when(studentRepository.existsById(99L))
                .thenReturn(false);

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> semesterService.getSemesters(99L, null)
        );

        // VERIFY
        verify(studentRepository).existsById(99L);
        verifyNoInteractions(semesterRepository);
    }

    // -----------------------------------------------
    // 3. GET SEMESTER BY ID
    // -----------------------------------------------

    @Test
    void shouldReturnSemesterWhenItBelongsToStudent() {

        // ARRANGE
        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository.findByIdAndStudentId(10L, 1L))
                .thenReturn(Optional.of(semester));

        // ACT
        SemesterResponse response =
                semesterService.getSemesterById(1L, 10L);

        // ASSERT
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(1L, response.studentId());
        assertEquals(2, response.yearLevel());
        assertEquals(1, response.semesterNumber());

        // VERIFY
        verify(studentRepository).existsById(1L);
        verify(semesterRepository).findByIdAndStudentId(10L, 1L);
    }

    @Test
    void shouldThrowExceptionWhenStudentDoesNotExistForSemesterLookup() {

        // ARRANGE
        when(studentRepository.existsById(99L))
                .thenReturn(false);

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> semesterService.getSemesterById(99L, 10L)
        );

        // VERIFY
        verifyNoInteractions(semesterRepository);
    }

    @Test
    void shouldThrowExceptionWhenSemesterDoesNotBelongToStudent() {

        // ARRANGE
        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository.findByIdAndStudentId(99L, 1L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> semesterService.getSemesterById(1L, 99L)
        );

        // VERIFY
        verify(semesterRepository).findByIdAndStudentId(99L, 1L);
    }

    // -----------------------------------------------
    // 4. UPDATE SEMESTER
    // -----------------------------------------------

    @Test
    void shouldUpdateSemesterWhenItExists() {

        // ARRANGE
        SemesterRequest request = new SemesterRequest(3, 2);

        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository.findByIdAndStudentId(10L, 1L))
                .thenReturn(Optional.of(semester));

        when(semesterRepository.save(any(Semester.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        SemesterResponse response =
                semesterService.updateSemester(1L, 10L, request);

        // ASSERT
        assertEquals(3, response.yearLevel());
        assertEquals(2, response.semesterNumber());
        assertEquals(10L, response.id());

        // VERIFY
        verify(semesterRepository).save(semester);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingSemester() {

        // ARRANGE
        SemesterRequest request = new SemesterRequest(3, 2);

        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository.findByIdAndStudentId(99L, 1L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> semesterService.updateSemester(1L, 99L, request)
        );

        // VERIFY
        verify(semesterRepository, never()).save(any(Semester.class));
    }

    // -----------------------------------------------
    // 5. DELETE SEMESTER
    // -----------------------------------------------

    @Test
    void shouldDeleteSemesterWhenItExists() {

        // ARRANGE
        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository.findByIdAndStudentId(10L, 1L))
                .thenReturn(Optional.of(semester));

        // ACT
        semesterService.deleteSemester(1L, 10L);

        // ASSERT + VERIFY
        verify(semesterRepository).delete(semester);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingSemester() {

        // ARRANGE
        when(studentRepository.existsById(1L))
                .thenReturn(true);

        when(semesterRepository.findByIdAndStudentId(99L, 1L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> semesterService.deleteSemester(1L, 99L)
        );

        // VERIFY
        verify(semesterRepository, never()).delete(any(Semester.class));
    }
}
