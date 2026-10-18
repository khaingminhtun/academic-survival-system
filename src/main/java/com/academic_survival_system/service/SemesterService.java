package com.academic_survival_system.service;


import com.academic_survival_system.dto.request.SemesterRequest;
import com.academic_survival_system.dto.response.SemesterResponse;

import java.util.List;

public interface SemesterService {

    SemesterResponse createSemester(
            Long studentId,
            SemesterRequest request
    );

    List<SemesterResponse> getSemesters(
            Long studentId,
            String academicYear
    );

    SemesterResponse getSemesterById(
            Long studentId,
            Long semesterId
    );

    SemesterResponse updateSemester(
            Long studentId,
            Long semesterId,
            SemesterRequest request
    );

    void deleteSemester(Long studentId, Long semesterId);
}
