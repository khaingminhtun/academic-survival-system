package com.academic_survival_system.dto;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubjectRequest(

        @NotBlank(message = "Subject code is required")
        @Size(max = 20)
        String subjectCode,

        @NotBlank(message = "Subject name is required")
        @Size(max = 100)
        String name,

        @NotNull(message = "Credit hours are required")
        @Min(value = 1, message = "Credit hours must be at least 1")
        Integer creditHours

) {}