package com.academic_survival_system.dto.response;

import lombok.Builder;

@Builder
public record StudentResponse(
        Long id,
        String studentId,
        String fullName,
        String department
) {}
