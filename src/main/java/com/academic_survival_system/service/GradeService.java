package com.academic_survival_system.service;

import com.academic_survival_system.dto.GradeRequest;
import com.academic_survival_system.dto.GradeResponse;

import java.util.List;

public interface GradeService {

    GradeResponse createGrade(GradeRequest request);

    List<GradeResponse> getAllGrades();

    GradeResponse getGradeById(Long id);

    List<GradeResponse> getGradesByStudent(Long studentId);

    GradeResponse updateGrade(Long id, GradeRequest request);

    void deleteGrade(Long id);
}
