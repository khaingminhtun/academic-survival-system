package com.academic_survival_system.controller;


import com.academic_survival_system.dto.request.SubjectRequest;
import com.academic_survival_system.dto.response.SubjectResponse;
import com.academic_survival_system.service.SubjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/semesters/{semesterId}/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    // Reference implementation
    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(
            @PathVariable Long semesterId,
            @Valid @RequestBody SubjectRequest request) {

        SubjectResponse response =
                subjectService.createSubject(semesterId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // TODO: GET /api/v1/semesters/{semesterId}/subjects
    // TODO: GET /api/v1/semesters/{semesterId}/subjects/{subjectId}
    // TODO: PUT /api/v1/semesters/{semesterId}/subjects/{subjectId}
    // TODO: DELETE /api/v1/semesters/{semesterId}/subjects/{subjectId}
}
