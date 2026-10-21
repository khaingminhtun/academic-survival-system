package com.academic_survival_system.dto.request;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SubjectRequest(

        @Positive(message = "Subject ID must be positive")
        Long subjectId,

        @NotBlank(message = "Subject code is required")
        @Size(max = 30, message = "Subject code must not exceed 30 characters")
        String subjectCode,

        @NotBlank(message = "Subject name is required")
        @Size(max = 100, message = "Subject name must not exceed 100 characters")
        String subjectName,

        @NotNull(message = "Mark is required")
        @DecimalMin(value = "0.0", message = "Mark cannot be below 0")
        @DecimalMax(value = "100.0", message = "Mark cannot exceed 100")
        BigDecimal mark

) {}
