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


    @PostMapping("/semesters/{semesterId}/results/calculate")
    public ResponseEntity<AcademicResultResponse> calculateResult(
            @PathVariable Long semesterId) {

        AcademicResultResponse response =
                academicResultService.calculateResult(semesterId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @GetMapping("/semesters/{semesterId}/results")
    public ResponseEntity<AcademicResultResponse> getLatestResult(
            @PathVariable Long semesterId) {

        AcademicResultResponse response =
                academicResultService.getLatestResult(semesterId);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/semesters/{semesterId}/results/history")
    public ResponseEntity<List<AcademicResultResponse>> getResultHistory(
            @PathVariable Long semesterId) {

        List<AcademicResultResponse> responses =
                academicResultService.getResultHistory(semesterId);

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/results/{resultId}")
    public ResponseEntity<AcademicResultResponse> getResultById(
            @PathVariable Long resultId) {

        AcademicResultResponse response =
                academicResultService.getResultById(resultId);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/students/{studentId}/results")
    public ResponseEntity<List<AcademicResultResponse>> getStudentResults(
            @PathVariable Long studentId) {

        List<AcademicResultResponse> responses =
                academicResultService.getStudentResults(studentId);

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/results/{resultId}/risk-subjects")
    public ResponseEntity<List<RiskSubjectResponse>> getRiskSubjects(
            @PathVariable Long resultId) {

        List<RiskSubjectResponse> responses =
                academicResultService.getRiskSubjects(resultId);

        return ResponseEntity.ok(responses);
    }
}

