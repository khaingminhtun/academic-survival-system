package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.StudentRequest;
import com.academic_survival_system.dto.response.StudentResponse;
import com.academic_survival_system.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {


    @Override
    public StudentResponse createStudent(StudentRequest request) {
        return null;
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return List.of();
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        return null;
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        return null;
    }

    @Override
    public void deleteStudent(Long id) {

    }
}

