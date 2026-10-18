package com.academic_survival_system.repository;

import com.academic_survival_system.model.AcademicResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicResultRepository
        extends JpaRepository<AcademicResult, Long> {

    List<AcademicResult> findByStudent_IdOrderByCalculatedAtDesc(
            Long studentId
    );
}