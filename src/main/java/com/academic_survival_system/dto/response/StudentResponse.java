package com.academic_survival_system.dto.response;

public record StudentResponse(
        Long id,
        String studentId,
        String fullName,
        String department
) {}
