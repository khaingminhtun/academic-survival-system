package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.StudentRequest;
import com.academic_survival_system.dto.response.StudentResponse;
import com.academic_survival_system.exception.DuplicateResourceException;
import com.academic_survival_system.exception.ResourceNotFoundException;
import com.academic_survival_system.model.Student;
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
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentRequest request;

    @BeforeEach
    void setUp(){
        request = new StudentRequest(
                "STU001",
                "Aung Aung",
                "Information Technology"
        );

        student = new Student();
        student.setId(1L);
        student.setStudentId("STU001");
        student.setFullName("Aung Aung");
        student.setDepartment("Information Technology");
    }

    //1. create student successfully
    // 1. Create student successfully
    @Test
    void shouldCreateStudentWhenStudentIdDoesNotExist() {
        when(studentRepository.existsByStudentId("STU001"))
                .thenReturn(false);

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> {
                    Student saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        StudentResponse response = studentService.createStudent(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("STU001", response.studentId());
        assertEquals("Aung Aung", response.fullName());
        assertEquals(
                "Information Technology",
                response.department()
        );

        verify(studentRepository).existsByStudentId("STU001");
        verify(studentRepository).save(any(Student.class));
    }

    // 2. Reject duplicate student ID during creation
    @Test
    void shouldThrowExceptionWhenCreatingDuplicateStudentId() {
        when(studentRepository.existsByStudentId("STU001"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> studentService.createStudent(request)
        );

        verify(studentRepository, never()).save(any(Student.class));
    }

    // 3. Get all students
    @Test
    void shouldReturnAllStudents() {
        Student secondStudent = new Student();
        secondStudent.setId(2L);
        secondStudent.setStudentId("STU002");
        secondStudent.setFullName("Su Su");
        secondStudent.setDepartment("Computer Science");

        when(studentRepository.findAll())
                .thenReturn(List.of(student, secondStudent));

        List<StudentResponse> responses =
                studentService.getAllStudents();

        assertEquals(2, responses.size());

        assertEquals("STU001", responses.get(0).studentId());
        assertEquals("STU002", responses.get(1).studentId());

        verify(studentRepository).findAll();
    }

    // 4. Return an empty list when there are no students
    @Test
    void shouldReturnEmptyListWhenNoStudentsExist() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<StudentResponse> responses =
                studentService.getAllStudents();

        assertTrue(responses.isEmpty());

        verify(studentRepository).findAll();
    }

    // 5. Get student by ID successfully
    @Test
    void shouldReturnStudentWhenIdExists() {
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        StudentResponse response =
                studentService.getStudentById(1L);

        assertEquals(1L, response.id());
        assertEquals("STU001", response.studentId());
        assertEquals("Aung Aung", response.fullName());

        verify(studentRepository).findById(1L);
    }

    // 6. Throw exception when student ID is not found
    @Test
    void shouldThrowExceptionWhenGettingMissingStudent() {
        when(studentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.getStudentById(99L)
        );

        verify(studentRepository).findById(99L);
    }

    // 7. Update student successfully
    @Test
    void shouldUpdateStudentWhenIdExistsAndStudentIdIsAvailable() {
        StudentRequest updateRequest = new StudentRequest(
                "STU009",
                "Aung Aung Updated",
                "Software Engineering"
        );

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(studentRepository.existsByStudentIdAndIdNot(
                "STU009", 1L
        )).thenReturn(false);

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StudentResponse response =
                studentService.updateStudent(1L, updateRequest);

        assertEquals(1L, response.id());
        assertEquals("STU009", response.studentId());
        assertEquals("Aung Aung Updated", response.fullName());
        assertEquals("Software Engineering", response.department());

        verify(studentRepository).findById(1L);
        verify(studentRepository)
                .existsByStudentIdAndIdNot("STU009", 1L);
        verify(studentRepository).save(student);
    }

    // 8. Reject a duplicate student ID during update
    @Test
    void shouldThrowExceptionWhenUpdatingToDuplicateStudentId() {
        StudentRequest updateRequest = new StudentRequest(
                "STU002",
                "Aung Aung",
                "Information Technology"
        );

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(studentRepository.existsByStudentIdAndIdNot(
                "STU002", 1L
        )).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> studentService.updateStudent(1L, updateRequest)
        );

        verify(studentRepository, never()).save(any(Student.class));
    }

    // 9. Delete student successfully
    @Test
    void shouldDeleteStudentWhenIdExists() {
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        verify(studentRepository).findById(1L);
        verify(studentRepository).delete(student);
    }

    // 10. Do not delete when student does not exist
    @Test
    void shouldThrowExceptionWhenDeletingMissingStudent() {
        when(studentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.deleteStudent(99L)
        );

        verify(studentRepository, never()).delete(any(Student.class));
    }



}
