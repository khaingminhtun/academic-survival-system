package com.academic_survival_system.controller;


import com.academic_survival_system.dto.response.AcademicResultResponse;
import com.academic_survival_system.dto.response.RiskSubjectResponse;
import com.academic_survival_system.service.AcademicResultService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AcademicResultController {

    private final AcademicResultService academicResultService;

    // Reference implementation
    @PostMapping("/semesters/{semesterId}/results/calculate")
    public ResponseEntity<AcademicResultResponse> calculateResult(
            @PathVariable Long semesterId) {

        AcademicResultResponse response =
                academicResultService.calculateResult(semesterId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // TODO: GET /api/v1/semesters/{semesterId}/results
    // Get the latest result report for a semester
    @GetMapping("/semesters/{semesterId}/results")
    public ResponseEntity<AcademicResultResponse> getLatestResult(
            @PathVariable Long semesterId) {

        AcademicResultResponse response =
                academicResultService.getLatestResult(semesterId);

        return ResponseEntity.ok(response);
    }

    // TODO: GET /api/v1/semesters/{semesterId}/results/history
    // Get the result report history for a semester
    @GetMapping("/semesters/{semesterId}/results/history")
    public ResponseEntity<List<AcademicResultResponse>> getResultHistory(
            @PathVariable Long semesterId) {

        List<AcademicResultResponse> responses =
                academicResultService.getResultHistory(semesterId);

        return ResponseEntity.ok(responses);
    }

    // TODO: GET /api/v1/results/{resultId}
    // Get one academic result report by ID
    @GetMapping("/results/{resultId}")
    public ResponseEntity<AcademicResultResponse> getResultById(
            @PathVariable Long resultId) {

        AcademicResultResponse response =
                academicResultService.getResultById(resultId);

        return ResponseEntity.ok(response);
    }

    // TODO: GET /api/v1/students/{studentId}/results
    // Get all academic result reports for a student
    @GetMapping("/students/{studentId}/results")
    public ResponseEntity<List<AcademicResultResponse>> getStudentResults(
            @PathVariable Long studentId) {

        List<AcademicResultResponse> responses =
                academicResultService.getStudentResults(studentId);

        return ResponseEntity.ok(responses);
    }


    // TODO: GET /api/v1/results/{resultId}/risk-subjects
    // Get at-risk subjects for a specific result report
    @GetMapping("/results/{resultId}/risk-subjects")
    public ResponseEntity<List<RiskSubjectResponse>> getRiskSubjects(
            @PathVariable Long resultId) {

        List<RiskSubjectResponse> responses =
                academicResultService.getRiskSubjects(resultId);

        return ResponseEntity.ok(responses);
    }
}

