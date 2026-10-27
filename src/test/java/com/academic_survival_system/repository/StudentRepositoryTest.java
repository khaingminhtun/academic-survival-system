package com.academic_survival_system.repository;

import com.academic_survival_system.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;


    @Test
    void existsByStudentId_returnsTrue_whenStudentExists(){
        //arrange
        Student student = new Student();
        student.setStudentId("STU001");
        student.setFullName("Mg Mg");
        student.setDepartment("IST");

        studentRepository.saveAndFlush(student);

        //act
        boolean result = studentRepository.existsByStudentId("STU001");

        //Assert
        assertTrue(result);
    }


    @Test
    void existsByStudentId_returnsFalse_whenStudentDoesNotExist(){
        //Act
        boolean result = studentRepository.existsByStudentId("STU999");

        //Assert
        assertFalse(result);
    }

    @Test
    void existsByStudentIdAndIdNot_returnsFalse_whenOnlyMatchingStudentHasExcludedId() {
        // Arrange
        Student student = new Student();
        student.setStudentId("STU001");
        student.setFullName("Mg Mg");
        student.setDepartment("IST");

        Student savedStudent = studentRepository.saveAndFlush(student);

        // Act
        boolean result = studentRepository.existsByStudentIdAndIdNot(
                "STU001",
                savedStudent.getId()
        );

        // Assert
        assertFalse(result);
    }

    @Test
    void existsByStudentIdAndIdNot_returnsTrue_whenMatchingStudentHasDifferentId() {
        // Arrange
        Student student = new Student();
        student.setStudentId("STU001");
        student.setFullName("Mg Mg");
        student.setDepartment("IST");

        Student savedStudent = studentRepository.saveAndFlush(student);

        // Act
        boolean result = studentRepository.existsByStudentIdAndIdNot(
                "STU001",
                savedStudent.getId() + 1
        );

        // Assert
        assertTrue(result);
    }
}
