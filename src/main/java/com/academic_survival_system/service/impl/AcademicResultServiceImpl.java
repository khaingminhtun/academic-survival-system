package com.academic_survival_system.service.impl;

import com.academic_survival_system.calculaton.AcademicCalculator;
import com.academic_survival_system.dto.response.AcademicResultResponse;
import com.academic_survival_system.dto.response.RiskSubjectResponse;
import com.academic_survival_system.exception.ResourceNotFoundException;
import com.academic_survival_system.model.AcademicResult;
import com.academic_survival_system.model.RiskSubject;
import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Subject;
import com.academic_survival_system.repository.AcademicResultRepository;
import com.academic_survival_system.repository.SemesterRepository;
import com.academic_survival_system.service.AcademicResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicResultServiceImpl implements AcademicResultService {

    private final AcademicResultRepository academicResultRepository;
    private final SemesterRepository semesterRepository;
    private final AcademicCalculator academicCalculator;


    @Override
    public AcademicResultResponse calculateResult(Long semesterId) {

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semester not found with ID: " + semesterId
                ));

        List<Subject> subjects = semester.getSubjects();

        List<BigDecimal> marks = subjects.stream()
                .map(Subject::getMark)
                .toList();

        BigDecimal average =
                academicCalculator.calculateAverage(marks);

        String grade =
                academicCalculator.calculateGrade(average);

        String riskLevel =
                academicCalculator.checkAcademicRisk(average);

        String feedback =
                academicCalculator.generateFeedback(
                        average,
                        grade,
                        riskLevel
                );

        AcademicResult result = AcademicResult.builder()
                .semester(semester)
                .averageMark(average)
                .grade(grade)
                .feedback(feedback)
                .calculatedAt(LocalDateTime.now())
                .build();

        List<RiskSubject> riskSubjects = subjects.stream()
                .filter(subject ->
                        academicCalculator.checkAcademicRisk(
                                subject.getMark()
                        ).equals("HIGH")
                )
                .map(subject -> RiskSubject.builder()
                        .academicResult(result)
                        .subject(subject)
                        .build()
                )
                .toList();

        result.setRiskSubjects(new ArrayList<>(riskSubjects));

        AcademicResult savedResult =
                academicResultRepository.save(result);

        return toResponse(savedResult);


    }

    @Override
    public AcademicResultResponse getLatestResult(Long semesterId) {
        verifySemesterExists(semesterId);

        AcademicResult result = academicResultRepository
                .findFirstBySemester_IdOrderByCalculatedAtDesc(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No academic result found for semester ID: " + semesterId
                ));

        return toResponse(result);
    }

    @Override
    public List<AcademicResultResponse> getResultHistory(Long semesterId) {
        verifySemesterExists(semesterId);

        return academicResultRepository
                .findAllBySemester_IdOrderByCalculatedAtDesc(semesterId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AcademicResultResponse getResultById(Long resultId) {

        AcademicResult result = academicResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic result not found with ID: " + resultId
                ));

        return toResponse(result);
    }

    @Override
    public List<AcademicResultResponse> getStudentResults(Long studentId, String academicYear) {
        return academicResultRepository
                .findAllBySemester_Student_IdAndSemester_AcademicYearOrderByCalculatedAtDesc(
                        studentId,
                        academicYear
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AcademicResultResponse> getStudentResults(Long studentId) {
        return academicResultRepository
                .findAllBySemester_Student_IdOrderByCalculatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<RiskSubjectResponse> getRiskSubjects(Long resultId) {
        AcademicResult result = academicResultRepository
                .findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic result not found with ID: " + resultId
                ));

        return result.getRiskSubjects()
                .stream()
                .map(this::toRiskSubjectResponse)
                .toList();
    }

    private void verifySemesterExists(Long semesterId) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new ResourceNotFoundException(
                    "Semester not found with ID: " + semesterId
            );
        }
    }

    private AcademicResultResponse toResponse(AcademicResult result) {
        List<RiskSubjectResponse> riskSubjects = result.getRiskSubjects()
                .stream()
                .map(this::toRiskSubjectResponse)
                .toList();

        return new AcademicResultResponse(
                result.getId(),
                result.getSemester().getId(),
                result.getAverageMark(),
                result.getGrade(),
                riskSubjects,
                result.getFeedback(),
                result.getCalculatedAt()
        );
    }

    private RiskSubjectResponse toRiskSubjectResponse(RiskSubject riskSubject) {
        return new RiskSubjectResponse(
                riskSubject.getSubject().getId(),
                riskSubject.getSubject().getSubjectCode(),
                riskSubject.getSubject().getSubjectName(),
                riskSubject.getSubject().getMark()
        );
    }
}
