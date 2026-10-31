package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.StudentRequest;
import com.academic_survival_system.dto.response.StudentResponse;
import com.academic_survival_system.exception.DuplicateResourceException;
import com.academic_survival_system.exception.ResourceNotFoundException;
import com.academic_survival_system.model.Student;
import com.academic_survival_system.repository.StudentRepository;
import com.academic_survival_system.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByStudentId(request.studentId())) {
            throw new DuplicateResourceException(
                    "Student ID already exists: " + request.studentId()
            );
        }

        Student student = new Student();
        student.setStudentId(request.studentId());
        student.setFullName(request.fullName());
        student.setDepartment(request.department());

        return toResponse(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        return toResponse(findStudent(id));
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = findStudent(id);

        if (studentRepository.existsByStudentIdAndIdNot(
                request.studentId(), id)) {
            throw new DuplicateResourceException(
                    "Student ID already exists: " + request.studentId()
            );
        }

        student.setStudentId(request.studentId());
        student.setFullName(request.fullName());
        student.setDepartment(request.department());

        return toResponse(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = findStudent(id);
        studentRepository.delete(student);
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id
                ));
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentId(),
                student.getFullName(),
                student.getDepartment()
        );
    }
}