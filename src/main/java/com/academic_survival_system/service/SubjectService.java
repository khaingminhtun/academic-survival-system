package com.academic_survival_system.service;

import com.academic_survival_system.dto.SubjectRequest;
import com.academic_survival_system.dto.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse create(SubjectRequest request);

    List<SubjectResponse> findAll();

    SubjectResponse findById(Long id);

    SubjectResponse update(Long id, SubjectRequest request);

    void delete(Long id);
}
