package com.academic_survival_system.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SemesterRequest(

        @NotNull(message = "Year level is required")
        @Min(value = 1, message = "Year level must be at least 1")
        @Max(value = 6, message = "Year level must not exceed 6")
        Integer yearLevel,

        @NotNull(message = "Semester number is required")
        @Min(value = 1, message = "Semester number must be 1 or 2")
        @Max(value = 2, message = "Semester number must be 1 or 2")
        Integer semesterNumber

) {}