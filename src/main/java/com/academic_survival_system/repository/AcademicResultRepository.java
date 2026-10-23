package com.academic_survival_system.repository;



import com.academic_survival_system.model.AcademicResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicResultRepository extends JpaRepository<AcademicResult, Long> {

    Optional<AcademicResult>
    findFirstBySemester_IdOrderByCalculatedAtDesc(Long semesterId);

    List<AcademicResult>
    findAllBySemester_IdOrderByCalculatedAtDesc(Long semesterId);



    List<AcademicResult> findAllBySemester_Student_IdOrderByCalculatedAtDesc(Long studentId);
}
