package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.SemesterRequest;
import com.academic_survival_system.dto.response.SemesterResponse;
import com.academic_survival_system.service.SemesterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SemesterServiceImpl implements SemesterService {
    @Override
    public SemesterResponse createSemester(Long studentId, SemesterRequest request) {
        return null;
    }

    @Override
    public List<SemesterResponse> getSemesters(Long studentId, String academicYear) {
        return List.of();
    }

    @Override
    public SemesterResponse getSemesterById(Long studentId, Long semesterId) {
        return null;
    }

    @Override
    public SemesterResponse updateSemester(Long studentId, Long semesterId, SemesterRequest request) {
        return null;
    }

    @Override
    public void deleteSemester(Long studentId, Long semesterId) {

    }
}
