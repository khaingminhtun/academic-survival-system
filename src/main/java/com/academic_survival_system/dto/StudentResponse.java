package com.academic_survival_system.dto;



public record StudentResponse(
        Long id,
        String studentNumber,
        String fullName,
        String email
) {}