package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.StudentRequest;
import com.academic_survival_system.dto.StudentResponse;
import com.academic_survival_system.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Override
    public StudentResponse create(StudentRequest request) {
        return null;
    }

    @Override
    public List<StudentResponse> findAll() {
        return List.of();
    }

    @Override
    public StudentResponse findById(Long id) {
        return null;
    }

    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
