package com.academic_survival_system.service;


import com.academic_survival_system.dto.request.SubjectRequest;
import com.academic_survival_system.dto.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    List<SubjectResponse> createSubjects(
            Long semesterId,
            List<SubjectRequest> requests
    );

    List<SubjectResponse> getAllSubjects(Long semesterId);

    SubjectResponse getSubjectById(Long semesterId, Long subjectId);

    SubjectResponse updateSubject(
            Long semesterId,
            Long subjectId,
            SubjectRequest request
    );

    List<SubjectResponse> updateSubjects(Long semesterId,List<SubjectRequest> requests);

    void deleteSubject(Long semesterId, Long subjectId);
}
