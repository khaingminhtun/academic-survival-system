package com.academic_survival_system.dto;


import java.math.BigDecimal;

public record MarkResponse(
        Long id,
        Long studentId,
        Long subjectId,
        BigDecimal mark
) {
}

