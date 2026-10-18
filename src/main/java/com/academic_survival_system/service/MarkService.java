package com.academic_survival_system.service;

import com.academic_survival_system.dto.MarkRequest;
import com.academic_survival_system.dto.MarkResponse;

import java.util.List;

public interface MarkService {

    MarkResponse create(MarkRequest request);

    List<MarkResponse> findAll();

    MarkResponse findById(Long id);

    List<MarkResponse> findByStudentId(Long studentId);

    MarkResponse update(Long id, MarkRequest request);

    void delete(Long id);
}