package com.academic_survival_system.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AcademicResultResponse(
        Long id,
        Long studentId,
        BigDecimal averageMark,
        String grade,
        String riskLevel,
        LocalDateTime calculatedAt,
        String feedback
) {
}
