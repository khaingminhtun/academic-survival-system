package com.academic_survival_system.service;


import com.academic_survival_system.dto.response.AcademicResultResponse;
import com.academic_survival_system.dto.response.RiskSubjectResponse;

import java.util.List;

public interface AcademicResultService {

    AcademicResultResponse calculateResult(Long semesterId);

    AcademicResultResponse getLatestResult(Long semesterId);

    List<AcademicResultResponse> getResultHistory(Long semesterId);

    AcademicResultResponse getResultById(Long resultId);



    List<AcademicResultResponse> getStudentResults(Long studentId);

    List<RiskSubjectResponse> getRiskSubjects(Long resultId);
}
