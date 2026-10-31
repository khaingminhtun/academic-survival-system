package com.academic_survival_system.dto.response;


import java.math.BigDecimal;

public record RiskSubjectResponse(
        Long subjectId,
        String subjectCode,
        String subjectName,
        BigDecimal mark
) {}