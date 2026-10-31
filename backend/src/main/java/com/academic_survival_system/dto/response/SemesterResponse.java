package com.academic_survival_system.dto.response;

public record SemesterResponse(
        Long id,
        Long studentId,
        Integer yearLevel,
        Integer semesterNumber
) {}