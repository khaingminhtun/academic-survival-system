package com.academic_survival_system.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SemesterRequest(

        @NotBlank(message = "Semester name is required")
        @Size(max = 50, message = "Semester name must not exceed 50 characters")
        String semesterName,

        @NotBlank(message = "Academic year is required")
        @Size(max = 20, message = "Academic year must not exceed 20 characters")
        String academicYear

) {}