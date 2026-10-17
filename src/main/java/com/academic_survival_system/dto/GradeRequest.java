package com.academic_survival_system.dto;



import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GradeRequest(

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Marks are required")
        @DecimalMin(value = "0.0", message = "Marks cannot be negative")
        @DecimalMax(value = "100.0", message = "Marks cannot exceed 100")
        BigDecimal marks

) {}