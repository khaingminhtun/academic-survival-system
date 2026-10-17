package com.academic_survival_system.dto;


import java.math.BigDecimal;

public record GradeResponse(
        Long id,
        Long studentId,
        String studentName,
        Long subjectId,
        String subjectName,
        BigDecimal marks,
        String letterGrade,
        BigDecimal gradePoints,
        BigDecimal averageMarks,
        boolean atAcademicRisk,
        String feedback
) {}
