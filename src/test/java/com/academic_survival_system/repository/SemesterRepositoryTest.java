
        package com.academic_survival_system.repository;

import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Student;

import com.academic_survival_system.repository.SemesterRepository;
import com.academic_survival_system.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SemesterRepositoryTest {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Helper method to create and save a student
    private Student createStudent(String studentId) {
        Student student = new Student();
        student.setStudentId(studentId);
        student.setFullName("Test Student");
        student.setDepartment("IST");

        return studentRepository.saveAndFlush(student);
    }

    // Helper method to create and save a semester
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

    @Test
    void findByStudentIdOrderByYearLevelAscSemesterNumberAsc_returnsSemestersInAscendingOrder() {
        // Arrange
        Student student = createStudent("STU001");

        createSemester(student, 2, 2);
        createSemester(student, 1, 2);
        createSemester(student, 1, 1);
        createSemester(student, 2, 1);

        // Act
        List<Semester> result =
                semesterRepository
                        .findByStudentIdOrderByYearLevelAscSemesterNumberAsc(
                                student.getId()
                        );

        // Assert
        assertEquals(4, result.size());

        assertEquals(1, result.get(0).getYearLevel());
        assertEquals(1, result.get(0).getSemesterNumber());

        assertEquals(1, result.get(1).getYearLevel());
        assertEquals(2, result.get(1).getSemesterNumber());

        assertEquals(2, result.get(2).getYearLevel());
        assertEquals(1, result.get(2).getSemesterNumber());

        assertEquals(2, result.get(3).getYearLevel());
        assertEquals(2, result.get(3).getSemesterNumber());
    }

    @Test
    void findByStudentIdOrderByYearLevelAscSemesterNumberAsc_returnsEmptyListWhenStudentHasNoSemesters() {
        // Arrange
        Student student = createStudent("STU002");

        // Act
        List<Semester> result =
                semesterRepository
                        .findByStudentIdOrderByYearLevelAscSemesterNumberAsc(
                                student.getId()
                        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByStudentIdAndYearLevelOrderBySemesterNumberAsc_returnsSemestersForRequestedYear() {
        // Arrange
        Student student = createStudent("STU003");

        createSemester(student, 1, 2);
        createSemester(student, 2, 1);
        createSemester(student, 1, 1);
        createSemester(student, 3, 1);

        // Act
        List<Semester> result =
                semesterRepository
                        .findByStudentIdAndYearLevelOrderBySemesterNumberAsc(
                                student.getId(),
                                1
                        );

        // Assert
        assertEquals(2, result.size());

        assertEquals(1, result.get(0).getYearLevel());
        assertEquals(1, result.get(0).getSemesterNumber());

        assertEquals(1, result.get(1).getYearLevel());
        assertEquals(2, result.get(1).getSemesterNumber());

        assertTrue(result.stream().allMatch(
                semester -> semester.getYearLevel().equals(1)
        ));
    }

    @Test
    void findByStudentIdAndYearLevelOrderBySemesterNumberAsc_returnsEmptyListWhenYearHasNoSemesters() {
        // Arrange
        Student student = createStudent("STU004");

        createSemester(student, 1, 1);

        // Act
        List<Semester> result =
                semesterRepository
                        .findByStudentIdAndYearLevelOrderBySemesterNumberAsc(
                                student.getId(),
                                3
                        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndStudentId_returnsSemesterWhenBothIdsMatch() {
        // Arrange
        Student student = createStudent("STU005");

        Semester saved = createSemester(student, 1, 1);

        // Act
        Optional<Semester> result =
                semesterRepository.findByIdAndStudentId(
                        saved.getId(),
                        student.getId()
                );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
        assertEquals(1, result.get().getYearLevel());
        assertEquals(1, result.get().getSemesterNumber());
    }

    @Test
    void findByIdAndStudentId_returnsEmptyWhenStudentDoesNotOwnSemester() {
        // Arrange
        Student student1 = createStudent("STU006");
        Student student2 = createStudent("STU007");

        Semester semester = createSemester(student1, 1, 1);

        // Act
        Optional<Semester> result =
                semesterRepository.findByIdAndStudentId(
                        semester.getId(),
                        student2.getId()
                );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndStudentId_returnsEmptyWhenSemesterDoesNotExist() {
        // Arrange
        Student student = createStudent("STU008");

        // Act
        Optional<Semester> result =
                semesterRepository.findByIdAndStudentId(
                        Long.MAX_VALUE,
                        student.getId()
                );

        // Assert
        assertTrue(result.isEmpty());
    }
}
