package com.academic_survival_system.service.impl;



import com.academic_survival_system.dto.SubjectRequest;
import com.academic_survival_system.dto.SubjectResponse;
import com.academic_survival_system.service.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Override
    public SubjectResponse createSubject(SubjectRequest request) {
        return null;
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {
        return null;
    }

    @Override
    public SubjectResponse getSubjectById(Long id) {
        return null;
    }

    @Override
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        return null;
    }

    @Override
    public void deleteSubject(Long id) {

    }
}
