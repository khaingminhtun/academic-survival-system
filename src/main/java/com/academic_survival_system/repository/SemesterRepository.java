package com.academic_survival_system.repository;

import com.academic_survival_system.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findByStudentId(Long studentId);

    List<Semester> findByStudentIdAndAcademicYear(
            Long studentId,
            String academicYear
    );

    Optional<Semester> findByIdAndStudentId(
            Long semesterId,
            Long studentId
    );
}
