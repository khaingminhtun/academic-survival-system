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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private Semester semester;

    @BeforeEach
    void setUp(){
        semester = new Semester();
        semester.setId(1L);
    }

    @Test
    void shouldThrowExceptionWhenCreatingFewerThanFiveSubjects(){
        //arrange
        List<SubjectRequest> requests = List.of(
                new SubjectRequest(null,"CS101","Programming",BigDecimal.valueOf(80.0)),
                new SubjectRequest(null,"CS102","Database",BigDecimal.valueOf(75.0))

        );

        //act + assert
        assertThrows(
                BadRequestException.class,
                ()-> subjectService.createSubjects(1L,requests)
        );

        //verify
        verifyNoInteractions(semesterRepository,subjectRepository);
    }

    @Test
    void shouldThrowExceptionWhenSubjectRequestsAreNull() {
        assertThrows(
                BadRequestException.class,
                () -> subjectService.createSubjects(1L, null)
        );

        verifyNoInteractions(semesterRepository, subjectRepository);
    }

    @Test
    void shouldThrowExceptionWhenSemesterDoesNotExist(){
        //arrange
        List<SubjectRequest> requests = createFiveSubjectRequests();

        when(semesterRepository.findById(99L))
                .thenReturn(Optional.empty());

        //act + assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> subjectService.createSubjects(99L,requests)
        );

        //verfiy
        verify(semesterRepository).findById(99L);
        verifyNoInteractions(subjectRepository);



    }


    @Test
    void shouldCreateFiveSubjectsWhenAllInputsAreValid(){
        //arrange
        List<SubjectRequest> requests = createFiveSubjectRequests();

        when(semesterRepository.findById(1L))
                .thenReturn(Optional.of(semester));

        when(subjectRepository.existsBySemesterIdAndSubjectCode(
                eq(1L),anyString()
        )).thenReturn(false);

        when(subjectRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    List<Subject> subjects = invocation.getArgument(0);

                    for(int i = 0; i < subjects.size();i++){
                        subjects.get(i).setId((long) i + 1);
                    }

                    return subjects;
                });

        // ACT
        List<SubjectResponse> responses =
                subjectService.createSubjects(1L, requests);

        // ASSERT
        assertNotNull(responses);
        assertEquals(5, responses.size());

        assertEquals("CS101", responses.get(0).subjectCode());
        assertEquals("Programming", responses.get(0).subjectName());
        assertEquals(BigDecimal.valueOf(80.0), responses.get(0).mark());

        assertEquals(1L, responses.get(0).semesterId());

        // VERIFY
        verify(semesterRepository).findById(1L);

        verify(subjectRepository, times(5))
                .existsBySemesterIdAndSubjectCode(
                        eq(1L), anyString()
                );

        verify(subjectRepository).saveAll(anyList());
    }

    private List<SubjectRequest> createFiveSubjectRequests() {
        return List.of(
                new SubjectRequest(null, "CS101", "Programming", BigDecimal.valueOf(80.0)),
                new SubjectRequest(null, "CS102", "Database", BigDecimal.valueOf(75.0)),
                new SubjectRequest(null, "CS103", "Networking", BigDecimal.valueOf(85.0)),
                new SubjectRequest(null, "CS104", "Operating Systems", BigDecimal.valueOf(70.0)),
                new SubjectRequest(null, "CS105", "Mathematics", BigDecimal.valueOf(90.0))
        );
    }


    @Test
    void shouldThrowExceptionWhenRequestContainsDuplicateSubjectCodes() {
        // ARRANGE
        List<SubjectRequest> requests = List.of(
                new SubjectRequest(null, "CS101", "Programming", BigDecimal.valueOf(80.0)),
                new SubjectRequest(null, "CS102", "Database", BigDecimal.valueOf(75.0)),
                new SubjectRequest(null, "CS101", "Another Subject", BigDecimal.valueOf(85.0)),
                new SubjectRequest(null, "CS104", "Operating Systems", BigDecimal.valueOf(70.0)),
                new SubjectRequest(null, "CS105", "Mathematics", BigDecimal.valueOf(90.0))
        );

        when(semesterRepository.findById(1L))
                .thenReturn(Optional.of(semester));

        // ACT + ASSERT
        assertThrows(
                DuplicateResourceException.class,
                () -> subjectService.createSubjects(1L, requests)
        );

        // VERIFY
        verify(subjectRepository, never()).saveAll(anyList());
    }

}
