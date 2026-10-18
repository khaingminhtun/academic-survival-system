package com.academic_survival_system.controller;


import com.academic_survival_system.dto.response.AcademicResultResponse;
import com.academic_survival_system.dto.response.RiskSubjectResponse;
import com.academic_survival_system.service.AcademicResultService;

import lombok.RequiredArgsConstructor;

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

        return ResponseEntity.ok(response);
    }

    // TODO: GET /api/v1/semesters/{semesterId}/results
    // TODO: GET /api/v1/semesters/{semesterId}/results/history
    // TODO: GET /api/v1/results/{resultId}
    // TODO: GET /api/v1/students/{studentId}/results

    // TODO: GET /api/v1/results/{resultId}/risk-subjects
    // Return List<RiskSubjectResponse>
}

