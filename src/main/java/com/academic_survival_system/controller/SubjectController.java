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


    @PostMapping
    public ResponseEntity<List<SubjectResponse>> createSubjects(
            @PathVariable Long semesterId,
            @Valid @RequestBody List<@Valid SubjectRequest> requests) {

        List<SubjectResponse> responses =
                subjectService.createSubjects(semesterId, requests);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responses);
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects(
            @PathVariable Long semesterId
    ) {
        return ResponseEntity.ok(
                subjectService.getAllSubjects(semesterId)
        );
    }


    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> getSubject(
            @PathVariable Long semesterId,
            @PathVariable Long subjectId
    ) {
        return ResponseEntity.ok(
                subjectService.getSubjectById(semesterId, subjectId)
        );
    }


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

    @PutMapping("/bulk")
    public ResponseEntity<List<SubjectResponse>> updateSubjects(
            @PathVariable Long semesterId,
            @Valid @RequestBody List<@Valid SubjectRequest> requests
    ) {
        return ResponseEntity.ok(
                subjectService.updateSubjects(
                        semesterId,
                        requests
                )
        );
    }


    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable Long semesterId,
            @PathVariable Long subjectId
    ) {
        subjectService.deleteSubject(semesterId, subjectId);

        return ResponseEntity.noContent().build();
    }
}
