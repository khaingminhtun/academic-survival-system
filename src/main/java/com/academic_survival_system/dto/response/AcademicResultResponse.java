package com.academic_survival_system.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AcademicResultResponse(
        Long id,
        Long semesterId,
        BigDecimal averageMark,
        String grade,
        List<RiskSubjectResponse> riskSubjects,
        String feedback,
        LocalDateTime calculatedAt
) {}