package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.SubjectRequest;
import com.academic_survival_system.dto.SubjectResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService implements com.academic_survival_system.service.SubjectService {
    @Override
    public SubjectResponse create(SubjectRequest request) {
        return null;
    }

    @Override
    public List<SubjectResponse> findAll() {
        return List.of();
    }

    @Override
    public SubjectResponse findById(Long id) {
        return null;
    }

    @Override
    public SubjectResponse update(Long id, SubjectRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
