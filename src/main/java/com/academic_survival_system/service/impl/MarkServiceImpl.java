package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.MarkRequest;
import com.academic_survival_system.dto.MarkResponse;
import com.academic_survival_system.service.MarkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkServiceImpl implements MarkService {
    @Override
    public MarkResponse create(MarkRequest request) {
        return null;
    }

    @Override
    public List<MarkResponse> findAll() {
        return List.of();
    }

    @Override
    public MarkResponse findById(Long id) {
        return null;
    }

    @Override
    public List<MarkResponse> findByStudentId(Long studentId) {
        return List.of();
    }

    @Override
    public MarkResponse update(Long id, MarkRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
