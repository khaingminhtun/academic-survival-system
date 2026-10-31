package com.academic_survival_system.repository;

import com.academic_survival_system.model.AcademicResult;
import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Student;
import com.academic_survival_system.repository.AcademicResultRepository;
import com.academic_survival_system.repository.SemesterRepository;
import com.academic_survival_system.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AcademicResultRepositoryTest {

    @Autowired
    private AcademicResultRepository academicResultRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        academicResultRepository.deleteAll();
        semesterRepository.deleteAll();
        studentRepository.deleteAll();
    }

    // Helper methods

    private Student createStudent(String studentId) {
        Student student = new Student();
        student.setStudentId(studentId);
        student.setFullName("Test Student " + studentId);
        student.setDepartment("Information Science and Technology");

        return studentRepository.saveAndFlush(student);
    }

    private Semester createSemester(
            Student student,
            int yearLevel,
            int semesterNumber
    ) {
        Semester semester = new Semester();
        semester.setStudent(student);
        semester.setYearLevel(yearLevel);
        semester.setSemesterNumber(semesterNumber);

        return semesterRepository.saveAndFlush(semester);
    }

    private AcademicResult createResult(
            Semester semester,
            String averageMark,
            String grade,
            String feedback,
            LocalDateTime calculatedAt
    ) {
        AcademicResult result = new AcademicResult();
        result.setSemester(semester);
        result.setAverageMark(new BigDecimal(averageMark));
        result.setGrade(grade);
        result.setFeedback(feedback);
        result.setCalculatedAt(calculatedAt);

        return academicResultRepository.saveAndFlush(result);
    }

    // Tests for findFirstBySemester_IdOrderByCalculatedAtDesc()

    @Test
    void findFirstBySemester_IdOrderByCalculatedAtDesc_shouldReturnLatestResult() {
        Student student = createStudent("STU001");
        Semester semester = createSemester(student, 1, 1);

        createResult(
                semester,
                "65.00",
                "C",
                "Needs improvement",
                LocalDateTime.of(2026, 1, 10, 10, 0)
        );

        AcademicResult latestResult = createResult(
                semester,
                "85.00",
                "A",
                "Excellent performance",
                LocalDateTime.of(2026, 2, 10, 10, 0)
        );

        Optional<AcademicResult> result =
                academicResultRepository
                        .findFirstBySemester_IdOrderByCalculatedAtDesc(
                                semester.getId()
                        );

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(latestResult.getId());
        assertThat(result.get().getAverageMark())
                .isEqualByComparingTo("85.00");
        assertThat(result.get().getGrade()).isEqualTo("A");
    }

    @Test
    void findFirstBySemester_IdOrderByCalculatedAtDesc_shouldReturnEmptyWhenNoResultsExist() {
        Student student = createStudent("STU002");
        Semester semester = createSemester(student, 1, 1);

        Optional<AcademicResult> result =
                academicResultRepository
                        .findFirstBySemester_IdOrderByCalculatedAtDesc(
                                semester.getId()
                        );

        assertThat(result).isEmpty();
    }

    // Tests for findAllBySemester_IdOrderByCalculatedAtDesc()

    @Test
    void findAllBySemester_IdOrderByCalculatedAtDesc_shouldReturnResultsNewestFirst() {
        Student student = createStudent("STU003");
        Semester semester = createSemester(student, 1, 1);

        createResult(
                semester,
                "60.00",
                "C",
                "First calculation",
                LocalDateTime.of(2026, 1, 10, 10, 0)
        );

        AcademicResult newestResult = createResult(
                semester,
                "90.00",
                "A",
                "Third calculation",
                LocalDateTime.of(2026, 3, 10, 10, 0)
        );

        AcademicResult middleResult = createResult(
                semester,
                "75.00",
                "B",
                "Second calculation",
                LocalDateTime.of(2026, 2, 10, 10, 0)
        );

        List<AcademicResult> results =
                academicResultRepository
                        .findAllBySemester_IdOrderByCalculatedAtDesc(
                                semester.getId()
                        );

        assertThat(results).hasSize(3);
        assertThat(results)
                .extracting(AcademicResult::getId)
                .containsExactly(
                        newestResult.getId(),
                        middleResult.getId(),
                        // The first result was created before the other two.
                        results.get(2).getId()
                );

        assertThat(results)
                .extracting(AcademicResult::getCalculatedAt)
                .isSortedAccordingTo(
                        java.util.Comparator.reverseOrder()
                );
    }

    @Test
    void findAllBySemester_IdOrderByCalculatedAtDesc_shouldReturnEmptyListWhenNoResultsExist() {
        Student student = createStudent("STU004");
        Semester semester = createSemester(student, 1, 1);

        List<AcademicResult> results =
                academicResultRepository
                        .findAllBySemester_IdOrderByCalculatedAtDesc(
                                semester.getId()
                        );

        assertThat(results).isEmpty();
    }

    // Tests for findAllBySemester_Student_IdOrderByCalculatedAtDesc()

    @Test
    void findAllBySemester_Student_IdOrderByCalculatedAtDesc_shouldReturnAllStudentResultsNewestFirst() {
        Student student = createStudent("STU005");

        Semester firstSemester = createSemester(student, 1, 1);
        Semester secondSemester = createSemester(student, 1, 2);

        createResult(
                firstSemester,
                "60.00",
                "C",
                "Older result",
                LocalDateTime.of(2026, 1, 10, 10, 0)
        );

        AcademicResult newestResult = createResult(
                secondSemester,
                "90.00",
                "A",
                "Newest result",
                LocalDateTime.of(2026, 3, 10, 10, 0)
        );

        AcademicResult middleResult = createResult(
                firstSemester,
                "75.00",
                "B",
                "Middle result",
                LocalDateTime.of(2026, 2, 10, 10, 0)
        );

        List<AcademicResult> results =
                academicResultRepository
                        .findAllBySemester_Student_IdOrderByCalculatedAtDesc(
                                student.getId()
                        );

        assertThat(results).hasSize(3);
        assertThat(results)
                .extracting(AcademicResult::getId)
                .containsExactly(
                        newestResult.getId(),
                        middleResult.getId(),
                        results.get(2).getId()
                );

        assertThat(results)
                .extracting(AcademicResult::getCalculatedAt)
                .isSortedAccordingTo(
                        java.util.Comparator.reverseOrder()
                );
    }

    @Test
    void findAllBySemester_Student_IdOrderByCalculatedAtDesc_shouldReturnEmptyListWhenStudentHasNoResults() {
        Student student = createStudent("STU006");

        List<AcademicResult> results =
                academicResultRepository
                        .findAllBySemester_Student_IdOrderByCalculatedAtDesc(
                                student.getId()
                        );

        assertThat(results).isEmpty();
    }
}
