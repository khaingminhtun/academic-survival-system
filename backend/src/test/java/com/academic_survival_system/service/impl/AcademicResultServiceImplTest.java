
package com.academic_survival_system.service.impl;

import com.academic_survival_system.calculaton.AcademicCalculator;
import com.academic_survival_system.dto.response.AcademicResultResponse;
import com.academic_survival_system.dto.response.RiskSubjectResponse;
import com.academic_survival_system.exception.ResourceNotFoundException;
import com.academic_survival_system.model.AcademicResult;
import com.academic_survival_system.model.RiskSubject;
import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Subject;
import com.academic_survival_system.repository.AcademicResultRepository;
import com.academic_survival_system.repository.SemesterRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicResultServiceImplTest {

    @Mock
    private AcademicResultRepository academicResultRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private AcademicCalculator academicCalculator;

    @InjectMocks
    private AcademicResultServiceImpl academicResultService;

    private Semester semester;
    private Subject programming;
    private Subject database;
    private Subject mathematics;

    @BeforeEach
    void setUp() {
        programming = Subject.builder()
                .id(1L)
                .subjectCode("CS101")
                .subjectName("Programming")
                .mark(BigDecimal.valueOf(80))
                .build();

        database = Subject.builder()
                .id(2L)
                .subjectCode("CS102")
                .subjectName("Database")
                .mark(BigDecimal.valueOf(30))
                .build();

        mathematics = Subject.builder()
                .id(3L)
                .subjectCode("CS103")
                .subjectName("Mathematics")
                .mark(BigDecimal.valueOf(70))
                .build();

        List<Subject> subjects =
                new ArrayList<>(List.of(programming, database, mathematics));

        semester = Semester.builder()
                .id(10L)
                .subjects(subjects)
                .build();

        subjects.forEach(subject -> subject.setSemester(semester));
    }

    // -----------------------------------------------
    // 1. CALCULATE RESULT
    // -----------------------------------------------

    @Test
    void shouldCalculateAndSaveAcademicResult() {

        // ARRANGE
        BigDecimal average = BigDecimal.valueOf(60);

        when(semesterRepository.findById(10L))
                .thenReturn(Optional.of(semester));

        when(academicCalculator.calculateAverage(anyList()))
                .thenReturn(average);

        when(academicCalculator.calculateGrade(average))
                .thenReturn("B");

        when(academicCalculator.checkAcademicRisk(average))
                .thenReturn("LOW");

        when(academicCalculator.checkAcademicRisk(
                BigDecimal.valueOf(80)))
                .thenReturn("LOW");

        when(academicCalculator.checkAcademicRisk(
                BigDecimal.valueOf(30)))
                .thenReturn("HIGH");

        when(academicCalculator.checkAcademicRisk(
                BigDecimal.valueOf(70)))
                .thenReturn("LOW");

        when(academicCalculator.generateFeedback(
                average, "B", "LOW"))
                .thenReturn("Good progress.");

        when(academicResultRepository.save(any(AcademicResult.class)))
                .thenAnswer(invocation -> {
                    AcademicResult result = invocation.getArgument(0);
                    result.setId(100L);
                    return result;
                });

        // ACT
        AcademicResultResponse response =
                academicResultService.calculateResult(10L);

        // ASSERT
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(10L, response.semesterId());
        assertEquals(0, average.compareTo(response.averageMark()));
        assertEquals("B", response.grade());
        assertEquals("Good progress.", response.feedback());
        assertNotNull(response.calculatedAt());

        assertEquals(1, response.riskSubjects().size());
        assertEquals("CS102",
                response.riskSubjects().get(0).subjectCode());

        // VERIFY
        verify(semesterRepository).findById(10L);
        verify(academicCalculator).calculateAverage(anyList());
        verify(academicCalculator).calculateGrade(average);
        verify(academicResultRepository).save(any(AcademicResult.class));
    }

    @Test
    void shouldThrowExceptionWhenCalculatingResultForMissingSemester() {

        // ARRANGE
        when(semesterRepository.findById(99L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> academicResultService.calculateResult(99L)
        );

        // VERIFY
        verifyNoInteractions(academicCalculator);
        verifyNoInteractions(academicResultRepository);
    }

    // -----------------------------------------------
    // 2. GET LATEST RESULT
    // -----------------------------------------------

    @Test
    void shouldReturnLatestResult() {

        // ARRANGE
        AcademicResult result = createResult(100L);

        when(semesterRepository.existsById(10L))
                .thenReturn(true);

        when(academicResultRepository
                .findFirstBySemester_IdOrderByCalculatedAtDesc(10L))
                .thenReturn(Optional.of(result));

        // ACT
        AcademicResultResponse response =
                academicResultService.getLatestResult(10L);

        // ASSERT
        assertEquals(100L, response.id());
        assertEquals("B", response.grade());

        // VERIFY
        verify(academicResultRepository)
                .findFirstBySemester_IdOrderByCalculatedAtDesc(10L);
    }

    @Test
    void shouldThrowExceptionWhenSemesterDoesNotExistForLatestResult() {

        // ARRANGE
        when(semesterRepository.existsById(99L))
                .thenReturn(false);

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> academicResultService.getLatestResult(99L)
        );

        // VERIFY
        verifyNoInteractions(academicResultRepository);
    }

    @Test
    void shouldThrowExceptionWhenLatestResultDoesNotExist() {

        // ARRANGE
        when(semesterRepository.existsById(10L))
                .thenReturn(true);

        when(academicResultRepository
                .findFirstBySemester_IdOrderByCalculatedAtDesc(10L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> academicResultService.getLatestResult(10L)
        );
    }

    // -----------------------------------------------
    // 3. GET RESULT HISTORY
    // -----------------------------------------------

    @Test
    void shouldReturnResultHistory() {

        // ARRANGE
        AcademicResult first = createResult(100L);
        AcademicResult second = createResult(101L);

        when(semesterRepository.existsById(10L))
                .thenReturn(true);

        when(academicResultRepository
                .findAllBySemester_IdOrderByCalculatedAtDesc(10L))
                .thenReturn(List.of(first, second));

        // ACT
        List<AcademicResultResponse> responses =
                academicResultService.getResultHistory(10L);

        // ASSERT
        assertEquals(2, responses.size());
        assertEquals(100L, responses.get(0).id());
        assertEquals(101L, responses.get(1).id());

        // VERIFY
        verify(academicResultRepository)
                .findAllBySemester_IdOrderByCalculatedAtDesc(10L);
    }

    @Test
    void shouldThrowExceptionWhenSemesterDoesNotExistForHistory() {

        // ARRANGE
        when(semesterRepository.existsById(99L))
                .thenReturn(false);

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> academicResultService.getResultHistory(99L)
        );

        // VERIFY
        verifyNoInteractions(academicResultRepository);
    }

    // -----------------------------------------------
    // 4. GET RESULT BY ID
    // -----------------------------------------------

    @Test
    void shouldReturnResultById() {

        // ARRANGE
        AcademicResult result = createResult(100L);

        when(academicResultRepository.findById(100L))
                .thenReturn(Optional.of(result));

        // ACT
        AcademicResultResponse response =
                academicResultService.getResultById(100L);

        // ASSERT
        assertEquals(100L, response.id());
        assertEquals("B", response.grade());

        // VERIFY
        verify(academicResultRepository).findById(100L);
    }

    @Test
    void shouldThrowExceptionWhenResultIdDoesNotExist() {

        // ARRANGE
        when(academicResultRepository.findById(999L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> academicResultService.getResultById(999L)
        );
    }

    // -----------------------------------------------
    // 5. GET STUDENT RESULTS
    // -----------------------------------------------

    @Test
    void shouldReturnAllResultsForStudent() {

        // ARRANGE
        when(academicResultRepository
                .findAllBySemester_Student_IdOrderByCalculatedAtDesc(1L))
                .thenReturn(List.of(
                        createResult(100L),
                        createResult(101L)
                ));

        // ACT
        List<AcademicResultResponse> responses =
                academicResultService.getStudentResults(1L);

        // ASSERT
        assertEquals(2, responses.size());
        assertEquals(100L, responses.get(0).id());
        assertEquals(101L, responses.get(1).id());

        // VERIFY
        verify(academicResultRepository)
                .findAllBySemester_Student_IdOrderByCalculatedAtDesc(1L);
    }

    @Test
    void shouldReturnEmptyListWhenStudentHasNoResults() {

        // ARRANGE
        when(academicResultRepository
                .findAllBySemester_Student_IdOrderByCalculatedAtDesc(1L))
                .thenReturn(List.of());

        // ACT
        List<AcademicResultResponse> responses =
                academicResultService.getStudentResults(1L);

        // ASSERT
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // -----------------------------------------------
    // 6. GET RISK SUBJECTS
    // -----------------------------------------------

    @Test
    void shouldReturnRiskSubjectsForResult() {

        // ARRANGE
        RiskSubject riskSubject = RiskSubject.builder()
                .academicResult(createResult(100L))
                .subject(database)
                .build();

        AcademicResult result = createResult(100L);
        result.setRiskSubjects(new ArrayList<>(List.of(riskSubject)));

        when(academicResultRepository.findById(100L))
                .thenReturn(Optional.of(result));

        // ACT
        List<RiskSubjectResponse> responses =
                academicResultService.getRiskSubjects(100L);

        // ASSERT
        assertEquals(1, responses.size());
        assertEquals(2L, responses.get(0).subjectId());
        assertEquals("CS102", responses.get(0).subjectCode());
        assertEquals("Database", responses.get(0).subjectName());
        assertEquals(
                0,
                BigDecimal.valueOf(30).compareTo(responses.get(0).mark())
        );

        // VERIFY
        verify(academicResultRepository).findById(100L);
    }

    @Test
    void shouldThrowExceptionWhenGettingRiskSubjectsForMissingResult() {

        // ARRANGE
        when(academicResultRepository.findById(999L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> academicResultService.getRiskSubjects(999L)
        );
    }

    // -----------------------------------------------
    // TEST DATA HELPER
    // -----------------------------------------------

    private AcademicResult createResult(Long id) {
        return AcademicResult.builder()
                .id(id)
                .semester(semester)
                .averageMark(BigDecimal.valueOf(60))
                .grade("B")
                .feedback("Good progress.")
                .calculatedAt(LocalDateTime.of(2026, 10, 27, 10, 0))
                .riskSubjects(new ArrayList<>())
                .build();
    }
}
