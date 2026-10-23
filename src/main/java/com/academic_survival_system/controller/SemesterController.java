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

    // POST /api/v1/students/{studentId}/semesters
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

    // GET /api/v1/students/{studentId}/semesters
    // Optional filter: ?yearLevel=1
    @GetMapping
    public ResponseEntity<List<SemesterResponse>> getSemesters(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer yearLevel) {

        List<SemesterResponse> response =
                semesterService.getSemesters(studentId, yearLevel);

        return ResponseEntity.ok(response);
    }

    // GET /api/v1/students/{studentId}/semesters/{semesterId}
    @GetMapping("/{semesterId}")
    public ResponseEntity<SemesterResponse> getSemesterById(
            @PathVariable Long studentId,
            @PathVariable Long semesterId) {

        SemesterResponse response =
                semesterService.getSemesterById(studentId, semesterId);

        return ResponseEntity.ok(response);
    }

    // PUT /api/v1/students/{studentId}/semesters/{semesterId}
    @PutMapping("/{semesterId}")
    public ResponseEntity<SemesterResponse> updateSemester(
            @PathVariable Long studentId,
            @PathVariable Long semesterId,
            @Valid @RequestBody SemesterRequest request) {

        SemesterResponse response =
                semesterService.updateSemester(
                        studentId, semesterId, request);

        return ResponseEntity.ok(response);
    }

    // DELETE /api/v1/students/{studentId}/semesters/{semesterId}
    @DeleteMapping("/{semesterId}")
    public ResponseEntity<Void> deleteSemester(
            @PathVariable Long studentId,
            @PathVariable Long semesterId) {

        semesterService.deleteSemester(studentId, semesterId);

        return ResponseEntity.noContent().build();
    }
}