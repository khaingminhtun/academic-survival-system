package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.SubjectRequest;
import com.academic_survival_system.dto.response.SubjectResponse;
import com.academic_survival_system.exception.DuplicateResourceException;
import com.academic_survival_system.exception.ResourceNotFoundException;
import com.academic_survival_system.model.Semester;
import com.academic_survival_system.model.Subject;
import com.academic_survival_system.repository.SemesterRepository;
import com.academic_survival_system.repository.SubjectRepository;
import com.academic_survival_system.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    private final SemesterRepository semesterRepository;

    @Override
    public SubjectResponse createSubject(Long semesterId, SubjectRequest request) {

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Semester not found with id: "+semesterId
                        ));

        if (subjectRepository.existsBySemesterIdAndSubjectCode(
                semesterId,
                request.subjectCode()
        )) {
            throw new DuplicateResourceException(
                    "Subject code already exists in this semester: "
                            + request.subjectCode()
            );
        }

        Subject subject = Subject.builder()
                .semester(semester)
                .subjectCode(request.subjectCode())
                .subjectName(request.subjectName())
                .mark(request.mark())
                .build();

        return toResponse(subjectRepository.save(subject));

    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects(Long semesterId) {

       verifySemesterExists(semesterId);

        return subjectRepository.findAllBySemester_Id(semesterId)
                .stream()
                .map(this::toResponse)
                .toList();


    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long semesterId, Long subjectId) {
        verifySemesterExists(semesterId);

        Subject subject = findSubject(semesterId, subjectId);

        return toResponse(subject);
    }

    @Override
    public SubjectResponse updateSubject(Long semesterId, Long subjectId, SubjectRequest request) {
        verifySemesterExists(semesterId);

        Subject subject = findSubject(semesterId, subjectId);

        if (subjectRepository.existsBySemester_IdAndSubjectCodeAndIdNot(
                semesterId,
                request.subjectCode(),
                subjectId
        )) {
            throw new DuplicateResourceException(
                    "Subject code already exists in this semester: "
                            + request.subjectCode()
            );
        }

        subject.setSubjectCode(request.subjectCode());
        subject.setSubjectName(request.subjectName());
        subject.setMark(request.mark());


        Subject updatedSubject = subjectRepository.save(subject);

        return toResponse(updatedSubject);
    }

    @Override
    public void deleteSubject(Long semesterId, Long subjectId) {
        verifySemesterExists(semesterId);

        Subject subject = findSubject(semesterId, subjectId);

        subjectRepository.delete(subject);

    }

    private Subject findSubject(Long semesterId, Long subjectId) {
        return subjectRepository
                .findByIdAndSemester_Id(subjectId, semesterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject not found with id: " + subjectId
                                + " in semester: " + semesterId
                ));
    }

    private void verifySemesterExists(Long semesterId) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new ResourceNotFoundException(
                    "Semester not found with id: " + semesterId
            );
        }
    }


    private SubjectResponse toResponse(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getSemester().getId(),
                subject.getSubjectCode(),
                subject.getSubjectName(),
                subject.getMark()
        );
    }
}
