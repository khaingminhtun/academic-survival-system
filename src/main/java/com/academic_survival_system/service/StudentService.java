package com.academic_survival_system.service;

import com.academic_survival_system.dto.StudentRequest;
import com.academic_survival_system.dto.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    List<StudentResponse> findAll();

    StudentResponse findById(Long id);

    StudentResponse update(Long id, StudentRequest request);

    void delete(Long id);
}
