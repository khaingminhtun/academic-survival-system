package com.academic_survival_system.repository;



import com.academic_survival_system.model.AcademicResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicResultRepository extends JpaRepository<AcademicResult, Long> {
}
