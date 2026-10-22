package com.academic_survival_system.repository;

import com.academic_survival_system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    boolean existsByStudentId(String studentId);
    boolean existsByStudentIdAndIdNot(String studentId, Long id);
}
