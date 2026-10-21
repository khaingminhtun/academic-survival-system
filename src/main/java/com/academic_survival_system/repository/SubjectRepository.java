package com.academic_survival_system.repository;

import com.academic_survival_system.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {

    List<Subject> findAllBySemester_Id(Long semesterId);

    Optional<Subject> findByIdAndSemester_Id(
            Long subjectId,
            Long semesterId
    );

    boolean existsBySemesterIdAndSubjectCode(
            Long semesterId,
            String subjectCode
    );

    boolean existsBySemester_IdAndSubjectCodeAndIdNot(
            Long semesterId,
            String subjectCode,
            Long subjectId
    );

    Optional<Subject> findBySemesterIdAndSubjectCode(
            Long semesterId,
            String subjectCode
    );

    boolean existsBySemesterIdAndSubjectCodeAndIdNot(
            Long semesterId,
            String subjectCode,
            Long subjectId
    );
}
