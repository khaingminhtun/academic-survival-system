package com.academic_survival_system.dto.response;


import java.math.BigDecimal;

public record SubjectResponse(
        Long id,
        Long semesterId,
        String subjectCode,
        String subjectName,
        BigDecimal mark
) {}