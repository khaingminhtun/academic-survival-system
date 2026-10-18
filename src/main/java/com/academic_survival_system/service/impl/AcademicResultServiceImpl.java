package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.response.AcademicResultResponse;
import com.academic_survival_system.dto.response.RiskSubjectResponse;
import com.academic_survival_system.service.AcademicResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AcademicResultServiceImpl implements AcademicResultService {
    @Override
    public AcademicResultResponse calculateResult(Long semesterId) {
        return null;
    }

    @Override
    public AcademicResultResponse getLatestResult(Long semesterId) {
        return null;
    }

    @Override
    public List<AcademicResultResponse> getResultHistory(Long semesterId) {
        return List.of();
    }

    @Override
    public AcademicResultResponse getResultById(Long resultId) {
        return null;
    }

    @Override
    public List<AcademicResultResponse> getStudentResults(Long studentId, String academicYear) {
        return List.of();
    }

    @Override
    public List<RiskSubjectResponse> getRiskSubjects(Long resultId) {
        return List.of();
    }
}
