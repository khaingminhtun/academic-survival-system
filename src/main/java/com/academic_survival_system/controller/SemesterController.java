package com.academic_survival_system.controller;


import com.academic_survival_system.dto.request.SemesterRequest;
import com.academic_survival_system.dto.response.SemesterResponse;
import com.academic_survival_system.service.SemesterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students/{studentId}/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterService semesterService;

    // Reference implementation
    @PostMapping
    public ResponseEntity<SemesterResponse> createSemester(
            @PathVariable Long studentId,
            @Valid @RequestBody SemesterRequest request) {

        SemesterResponse response =
                semesterService.createSemester(studentId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // TODO: GET /api/v1/students/{studentId}/semesters
    // Include optional @RequestParam academicYear for filtering.
    // TODO: GET /api/v1/students/{studentId}/semesters/{semesterId}
    // TODO: PUT /api/v1/students/{studentId}/semesters/{semesterId}
    // TODO: DELETE /api/v1/students/{studentId}/semesters/{semesterId}
}

