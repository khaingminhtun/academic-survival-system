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
    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects(
            @PathVariable Long semesterId
    ) {
        return ResponseEntity.ok(
                subjectService.getAllSubjects(semesterId)
        );
    }
    // TODO: GET /api/v1/semesters/{semesterId}/subjects/{subjectId}
    // GET: Get one subject
    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> getSubject(
            @PathVariable Long semesterId,
            @PathVariable Long subjectId
    ) {
        return ResponseEntity.ok(
                subjectService.getSubjectById(semesterId, subjectId)
        );
    }

    // TODO: PUT /api/v1/semesters/{semesterId}/subjects/{subjectId}
    // PUT: Update a subject
    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable Long semesterId,
            @PathVariable Long subjectId,
            @Valid @RequestBody SubjectRequest request
    ) {
        return ResponseEntity.ok(
                subjectService.updateSubject(
                        semesterId, subjectId, request
                )
        );
    }

    // TODO: DELETE /api/v1/semesters/{semesterId}/subjects/{subjectId}
    // DELETE: Delete a subject
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable Long semesterId,
            @PathVariable Long subjectId
    ) {
        subjectService.deleteSubject(semesterId, subjectId);

        return ResponseEntity.noContent().build();
    }
}
