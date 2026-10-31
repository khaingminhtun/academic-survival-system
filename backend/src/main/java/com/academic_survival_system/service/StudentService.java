package com.academic_survival_system.service;

import com.academic_survival_system.dto.request.StudentRequest;
import com.academic_survival_system.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(StudentRequest request);

    List<StudentResponse> getAllStudents();

    StudentResponse getStudentById(Long id);

    StudentResponse updateStudent(Long id, StudentRequest request);

    void deleteStudent(Long id);


}
