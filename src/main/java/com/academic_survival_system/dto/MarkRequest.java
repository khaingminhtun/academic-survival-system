package com.academic_survival_system.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MarkRequest(

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Mark is required")
        @DecimalMin(value = "0.00", message = "Mark cannot be less than 0")
        @DecimalMax(value = "100.00", message = "Mark cannot exceed 100")
        BigDecimal mark

) {
}
