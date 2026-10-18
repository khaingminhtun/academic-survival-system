package com.academic_survival_system.service;

import com.academic_survival_system.dto.AcademicResultResponse;

public interface GradeService {

    AcademicResultResponse calculate(Long studentId);

    AcademicResultResponse getLatestResult(Long studentId);
}
