package com.academic_survival_system.service;


import com.academic_survival_system.dto.request.SubjectRequest;
import com.academic_survival_system.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse createSubject(
            Long semesterId,
            SubjectRequest request
    );

    List<SubjectResponse> getSubjects(Long semesterId);

    SubjectResponse getSubjectById(Long semesterId, Long subjectId);

    SubjectResponse updateSubject(
            Long semesterId,
            Long subjectId,
            SubjectRequest request
    );

    void deleteSubject(Long semesterId, Long subjectId);
}
