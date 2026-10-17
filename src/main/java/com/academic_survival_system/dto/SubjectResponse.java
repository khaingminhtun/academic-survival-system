package com.academic_survival_system.dto;



public record SubjectResponse(
        Long id,
        String subjectCode,
        String name,
        Integer creditHours
) {}