
        package com.academic_survival_system.repository;

import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Student;
import com.academic_survival_system.model.Subject;

import com.academic_survival_system.repository.SemesterRepository;
import com.academic_survival_system.repository.StudentRepository;
import com.academic_survival_system.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Helper method to create and save a student and semester
    private Semester createSemester(
            String studentId,
            int yearLevel,
            int semesterNumber
    ) {
        Student student = new Student();
        student.setStudentId(studentId);
        student.setFullName("Test Student");
        student.setDepartment("IST");

        student = studentRepository.saveAndFlush(student);

        Semester semester = new Semester();
        semester.setStudent(student);
        semester.setYearLevel(yearLevel);
        semester.setSemesterNumber(semesterNumber);

        return semesterRepository.saveAndFlush(semester);
    }

    // Helper method to create and save a subject
    private Subject createSubject(
            Semester semester,
            String subjectCode,
            String subjectName,
            String mark
    ) {
        Subject subject = new Subject();
        subject.setSemester(semester);
        subject.setSubjectCode(subjectCode);
        subject.setSubjectName(subjectName);
        subject.setMark(new BigDecimal(mark));

        return subjectRepository.saveAndFlush(subject);
    }

    @Test
    void findAllBySemester_Id_returnsSubjectsForSemester() {
        // Arrange
        Semester semester1 = createSemester("STU001", 1, 1);
        Semester semester2 = createSemester("STU002", 1, 1);

        createSubject(semester1, "CS101", "Java", "80.00");
        createSubject(semester1, "CS102", "Database", "85.00");
        createSubject(semester2, "CS103", "Networking", "75.00");

        // Act
        List<Subject> result =
                subjectRepository.findAllBySemester_Id(
                        semester1.getId()
                );

        // Assert
        assertEquals(2, result.size());

        assertTrue(result.stream().allMatch(
                subject -> subject.getSemester().getId()
                        .equals(semester1.getId())
        ));
    }

    @Test
    void findByIdAndSemester_Id_returnsSubjectWhenBothMatch() {
        // Arrange
        Semester semester = createSemester("STU003", 1, 1);

        Subject saved = createSubject(
                semester, "CS201", "Java", "90.00"
        );

        // Act
        Optional<Subject> result =
                subjectRepository.findByIdAndSemester_Id(
                        saved.getId(),
                        semester.getId()
                );

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CS201", result.get().getSubjectCode());
    }

    @Test
    void findByIdAndSemester_Id_returnsEmptyWhenSemesterDoesNotMatch() {
        // Arrange
        Semester semester1 = createSemester("STU004", 1, 1);
        Semester semester2 = createSemester("STU005", 1, 1);

        Subject saved = createSubject(
                semester1, "CS301", "Java", "80.00"
        );

        // Act
        Optional<Subject> result =
                subjectRepository.findByIdAndSemester_Id(
                        saved.getId(),
                        semester2.getId()
                );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void existsBySemesterIdAndSubjectCode_returnsTrueWhenExists() {
        // Arrange
        Semester semester = createSemester("STU006", 1, 1);

        createSubject(semester, "CS401", "Java", "80.00");

        // Act
        boolean result =
                subjectRepository.existsBySemesterIdAndSubjectCode(
                        semester.getId(),
                        "CS401"
                );

        // Assert
        assertTrue(result);
    }

    @Test
    void existsBySemesterIdAndSubjectCode_returnsFalseWhenMissing() {
        // Arrange
        Semester semester = createSemester("STU007", 1, 1);

        // Act
        boolean result =
                subjectRepository.existsBySemesterIdAndSubjectCode(
                        semester.getId(),
                        "CS999"
                );

        // Assert
        assertFalse(result);
    }

    @Test
    void existsBySemester_IdAndSubjectCodeAndIdNot_returnsFalseWhenExcludingSameSubject() {
        // Arrange
        Semester semester = createSemester("STU008", 1, 1);

        Subject saved = createSubject(
                semester, "CS501", "Java", "80.00"
        );

        // Act
        boolean result =
                subjectRepository
                        .existsBySemester_IdAndSubjectCodeAndIdNot(
                                semester.getId(),
                                "CS501",
                                saved.getId()
                        );

        // Assert
        assertFalse(result);
    }

    @Test
    void existsBySemester_IdAndSubjectCodeAndIdNot_returnsTrueWhenMatchingSubjectIsNotExcluded() {
        // Arrange
        Semester semester = createSemester("STU009", 1, 1);

        Subject saved = createSubject(
                semester, "CS601", "Java", "80.00"
        );

        // Act
        boolean result =
                subjectRepository
                        .existsBySemester_IdAndSubjectCodeAndIdNot(
                                semester.getId(),
                                "CS601",
                                saved.getId() + 1
                        );

        // Assert
        assertTrue(result);
    }

    @Test
    void findBySemesterIdAndSubjectCode_returnsSubjectWhenExists() {
        // Arrange
        Semester semester = createSemester("STU010", 1, 1);

        createSubject(semester, "CS701", "Java", "88.00");

        // Act
        Optional<Subject> result =
                subjectRepository.findBySemesterIdAndSubjectCode(
                        semester.getId(),
                        "CS701"
                );

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Java", result.get().getSubjectName());
        assertEquals(
                new BigDecimal("88.00"),
                result.get().getMark()
        );
    }

    @Test
    void findBySemesterIdAndSubjectCode_returnsEmptyWhenMissing() {
        // Arrange
        Semester semester = createSemester("STU011", 1, 1);

        // Act
        Optional<Subject> result =
                subjectRepository.findBySemesterIdAndSubjectCode(
                        semester.getId(),
                        "CS999"
                );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void existsBySemesterIdAndSubjectCodeAndIdNot_returnsFalseWhenExcludingSameSubject() {
        // Arrange
        Semester semester = createSemester("STU012", 1, 1);

        Subject saved = createSubject(
                semester, "CS801", "Java", "80.00"
        );

        // Act
        boolean result =
                subjectRepository
                        .existsBySemesterIdAndSubjectCodeAndIdNot(
                                semester.getId(),
                                "CS801",
                                saved.getId()
                        );

        // Assert
        assertFalse(result);
    }
}