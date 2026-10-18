package com.academic_survival_system.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(

        @NotBlank(message = "Subject code is required")
        @Size(max = 30, message = "Subject code must not exceed 30 characters")
        String subjectCode,

        @NotBlank(message = "Subject name is required")
        @Size(max = 100, message = "Subject name must not exceed 100 characters")
        String subjectName

) {
}
