package com.academic_survival_system.dto;


public record StudentResponse(
        Long id,
        String studentId,
        String fullName,
        String email
) {
}
