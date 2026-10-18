package com.academic_survival_system.dto.request;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SubjectRequest(

        @NotBlank(message = "Subject code is required")
        @Size(max = 30, message = "Subject code must not exceed 30 characters")
        String subjectCode,

        @NotBlank(message = "Subject name is required")
        @Size(max = 100, message = "Subject name must not exceed 100 characters")
        String subjectName,

        @DecimalMin(value = "0.0", message = "Mark cannot be below 0")
        @DecimalMax(value = "100.0", message = "Mark cannot exceed 100")
        BigDecimal mark

) {}
