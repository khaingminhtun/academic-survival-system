package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.SubjectRequest;
import com.academic_survival_system.dto.response.SubjectResponse;
import com.academic_survival_system.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    @Override
    public SubjectResponse createSubject(Long semesterId, SubjectRequest request) {
        return null;
    }

    @Override
    public List<SubjectResponse> getSubjects(Long semesterId) {
        return List.of();
    }

    @Override
    public SubjectResponse getSubjectById(Long semesterId, Long subjectId) {
        return null;
    }

    @Override
    public SubjectResponse updateSubject(Long semesterId, Long subjectId, SubjectRequest request) {
        return null;
    }

    @Override
    public void deleteSubject(Long semesterId, Long subjectId) {

    }
}
