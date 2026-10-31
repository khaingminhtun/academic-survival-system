package com.academic_survival_system.service.impl;

import com.academic_survival_system.dto.request.SubjectRequest;
import com.academic_survival_system.dto.response.SubjectResponse;
import com.academic_survival_system.exception.BadRequestException;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    private final SemesterRepository semesterRepository;

    @Override
    @Transactional
    public List<SubjectResponse> createSubjects(
            Long semesterId,
            List<SubjectRequest> requests) {

        if (requests == null || requests.size() < 5) {
            throw new BadRequestException(
                    "At least 5 subjects are required to submit semester subjects"
            );
        }

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semester not found with ID: " + semesterId
                ));

        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException(
                    "Subject list cannot be empty"
            );
        }

        Set<String> subjectCodes = new HashSet<>();

        for (SubjectRequest request : requests) {

            String subjectCode = request.subjectCode();

            if (!subjectCodes.add(subjectCode)) {
                throw new DuplicateResourceException(
                        "Duplicate subject code in request: " + subjectCode
                );
            }

            if (subjectRepository.existsBySemesterIdAndSubjectCode(
                    semesterId, subjectCode)) {

                throw new DuplicateResourceException(
                        "Subject code already exists in this semester: "
                                + subjectCode
                );
            }
        }

        List<Subject> subjects = requests.stream()
                .map(request -> Subject.builder()
                        .semester(semester)
                        .subjectCode(request.subjectCode())
                        .subjectName(request.subjectName())
                        .mark(request.mark())
                        .build())
                .toList();

        List<Subject> savedSubjects =
                subjectRepository.saveAll(subjects);


        return savedSubjects.stream()
                .map(this::toResponse)
                .toList();
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

    @Transactional
    public List<SubjectResponse> updateSubjects(
            Long semesterId,
            List<SubjectRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException(
                    "At least one subject is required"
            );
        }

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semester not found with ID: " + semesterId
                ));

        Set<Long> subjectIds = new HashSet<>();
        Set<String> subjectCodes = new HashSet<>();

        // Validate IDs and duplicate codes
        for (SubjectRequest request : requests) {

            if (request.subjectId() == null) {
                throw new BadRequestException(
                        "Subject ID is required for updating subjects"
                );
            }

            if (!subjectIds.add(request.subjectId())) {
                throw new DuplicateResourceException(
                        "Duplicate subject ID in request: "
                                + request.subjectId()
                );
            }

            String code = request.subjectCode().trim();

            if (!subjectCodes.add(code)) {
                throw new DuplicateResourceException(
                        "Duplicate subject code in request: " + code
                );
            }
        }

        // Load and validate every subject before modifying any
        List<Subject> subjectsToUpdate = new ArrayList<>();

        for (SubjectRequest request : requests) {

            Subject subject = subjectRepository.findById(request.subjectId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Subject not found with ID: "
                                    + request.subjectId()
                    ));

            if (!subject.getSemester().getId().equals(semester.getId())) {
                throw new BadRequestException(
                        "Subject ID " + request.subjectId()
                                + " does not belong to semester ID " + semesterId
                );
            }

            subject.setSubjectCode(request.subjectCode().trim());
            subject.setSubjectName(request.subjectName().trim());
            subject.setMark(request.mark());

            subjectsToUpdate.add(subject);
        }

        // Check codes against records outside this update batch
        for (SubjectRequest request : requests) {

            String code = request.subjectCode().trim();

            boolean exists = subjectRepository
                    .existsBySemesterIdAndSubjectCodeAndIdNot(
                            semesterId,
                            code,
                            request.subjectId()
                    );

            if (exists) {
                Subject conflictingSubject = subjectRepository
                        .findBySemesterIdAndSubjectCode(semesterId, code)
                        .orElse(null);

                if (conflictingSubject != null
                        && !subjectIds.contains(conflictingSubject.getId())) {
                    throw new DuplicateResourceException(
                            "Subject code already exists in this semester: "
                                    + code
                    );
                }
            }
        }

        return subjectRepository.saveAll(subjectsToUpdate)
                .stream()
                .map(this::toResponse)
                .toList();
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
