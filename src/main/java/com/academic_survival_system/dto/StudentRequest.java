package com.academic_survival_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRequest(

        @NotBlank(message = "Student ID is required")
        @Size(max = 50, message = "Student ID must not exceed 50 characters")
        String studentId,

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email

) {
}
