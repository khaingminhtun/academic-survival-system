package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.SemesterRequest;
import com.academic_survival_system.dto.response.SemesterResponse;
import com.academic_survival_system.exception.ResourceNotFoundException;
import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Student;
import com.academic_survival_system.repository.SemesterRepository;
import com.academic_survival_system.repository.StudentRepository;
import com.academic_survival_system.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public SemesterResponse createSemester(
            Long studentId,
            SemesterRequest request) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + studentId
                ));

        Semester semester = Semester.builder()
                .student(student)
                .semesterName(request.semesterName())
                .academicYear(request.academicYear())
                .build();

        Semester savedSemester = semesterRepository.save(semester);

        return toResponse(savedSemester);
    }

    @Override
    public List<SemesterResponse> getSemesters(
            Long studentId,
            String academicYear) {

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException(
                    "Student not found with id: " + studentId
            );
        }

        List<Semester> semesters;

        if (academicYear == null || academicYear.isBlank()) {
            semesters =
                    semesterRepository.findByStudentId(studentId);
        } else {
            semesters =
                    semesterRepository
                            .findByStudentIdAndAcademicYear(
                                    studentId, academicYear);
        }

        return semesters.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SemesterResponse getSemesterById(
            Long studentId,
            Long semesterId) {

        Semester semester = findSemester(studentId, semesterId);

        return toResponse(semester);
    }

    @Override
    @Transactional
    public SemesterResponse updateSemester(
            Long studentId,
            Long semesterId,
            SemesterRequest request) {

        Semester semester = findSemester(studentId, semesterId);

        semester.setSemesterName(request.semesterName());
        semester.setAcademicYear(request.academicYear());

        Semester updatedSemester = semesterRepository.save(semester);

        return toResponse(updatedSemester);
    }

    @Override
    @Transactional
    public void deleteSemester(Long studentId, Long semesterId) {

        Semester semester = findSemester(studentId, semesterId);

        semesterRepository.delete(semester);
    }

    private Semester findSemester(
            Long studentId,
            Long semesterId) {

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException(
                    "Student not found with id: " + studentId
            );
        }

        return semesterRepository
                .findByIdAndStudentId(semesterId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semester not found with id: " + semesterId
                ));
    }

    private SemesterResponse toResponse(Semester semester) {
        return new SemesterResponse(
                semester.getId(),
                semester.getStudent().getId(),
                semester.getSemesterName(),
                semester.getAcademicYear()
        );
    }
}
