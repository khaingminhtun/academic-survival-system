
package com.academic_survival_system.controller;

import com.academic_survival_system.dto.response.AcademicResultResponse;
import com.academic_survival_system.dto.response.RiskSubjectResponse;
import com.academic_survival_system.service.AcademicResultService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AcademicResultController.class)
class AcademicResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AcademicResultService academicResultService;

    private static final Long SEMESTER_ID = 10L;
    private static final Long RESULT_ID = 100L;
    private static final Long STUDENT_ID = 1L;

    private static final LocalDateTime CALCULATED_AT =
            LocalDateTime.of(2026, 10, 27, 19, 30, 0);

    private RiskSubjectResponse createRiskSubjectResponse() {
        return new RiskSubjectResponse(
                5L,
                "CS102",
                "Data Structures",
                new BigDecimal("35.00")
        );
    }

    private AcademicResultResponse createAcademicResultResponse() {
        return new AcademicResultResponse(
                RESULT_ID,
                SEMESTER_ID,
                new BigDecimal("72.50"),
                "B",
                List.of(createRiskSubjectResponse()),
                "Focus on improving Data Structures.",
                CALCULATED_AT
        );
    }

    // 1. POST /api/v1/semesters/{semesterId}/results/calculate

    @Test
    void calculateResult_shouldReturn201Created() throws Exception {

        // Arrange
        when(academicResultService.calculateResult(SEMESTER_ID))
                .thenReturn(createAcademicResultResponse());

        // Act and Assert
        mockMvc.perform(post(
                        "/api/v1/semesters/{semesterId}/results/calculate",
                        SEMESTER_ID
                ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.semesterId").value(10))
                .andExpect(jsonPath("$.averageMark").value(72.50))
                .andExpect(jsonPath("$.grade").value("B"))
                .andExpect(jsonPath("$.feedback")
                        .value("Focus on improving Data Structures."))
                .andExpect(jsonPath("$.calculatedAt")
                        .value("2026-10-27T19:30:00"))
                .andExpect(jsonPath("$.riskSubjects.length()").value(1))
                .andExpect(jsonPath("$.riskSubjects[0].subjectCode")
                        .value("CS102"))
                .andExpect(jsonPath("$.riskSubjects[0].mark")
                        .value(35.00));

        verify(academicResultService).calculateResult(SEMESTER_ID);
    }

    // 2. GET /api/v1/semesters/{semesterId}/results

    @Test
    void getLatestResult_shouldReturn200AndAcademicResult()
            throws Exception {

        // Arrange
        when(academicResultService.getLatestResult(SEMESTER_ID))
                .thenReturn(createAcademicResultResponse());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/semesters/{semesterId}/results",
                        SEMESTER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.semesterId").value(10))
                .andExpect(jsonPath("$.averageMark").value(72.50))
                .andExpect(jsonPath("$.grade").value("B"))
                .andExpect(jsonPath("$.riskSubjects[0].subjectName")
                        .value("Data Structures"));

        verify(academicResultService).getLatestResult(SEMESTER_ID);
    }

    // 3. GET /api/v1/semesters/{semesterId}/results/history

    @Test
    void getResultHistory_shouldReturn200AndResultList()
            throws Exception {

        // Arrange
        AcademicResultResponse firstResult =
                createAcademicResultResponse();

        AcademicResultResponse secondResult =
                new AcademicResultResponse(
                        101L,
                        SEMESTER_ID,
                        new BigDecimal("80.00"),
                        "A",
                        List.of(),
                        "Excellent academic performance.",
                        CALCULATED_AT.plusDays(1)
                );

        when(academicResultService.getResultHistory(SEMESTER_ID))
                .thenReturn(List.of(firstResult, secondResult));

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/semesters/{semesterId}/results/history",
                        SEMESTER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].grade").value("B"))
                .andExpect(jsonPath("$[1].id").value(101))
                .andExpect(jsonPath("$[1].averageMark").value(80.00))
                .andExpect(jsonPath("$[1].grade").value("A"));

        verify(academicResultService).getResultHistory(SEMESTER_ID);
    }

    @Test
    void getResultHistory_whenEmpty_shouldReturn200AndEmptyList()
            throws Exception {

        // Arrange
        when(academicResultService.getResultHistory(SEMESTER_ID))
                .thenReturn(List.of());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/semesters/{semesterId}/results/history",
                        SEMESTER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(academicResultService).getResultHistory(SEMESTER_ID);
    }

    // 4. GET /api/v1/results/{resultId}

    @Test
    void getResultById_shouldReturn200AndAcademicResult()
            throws Exception {

        // Arrange
        when(academicResultService.getResultById(RESULT_ID))
                .thenReturn(createAcademicResultResponse());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/results/{resultId}",
                        RESULT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.averageMark").value(72.50))
                .andExpect(jsonPath("$.grade").value("B"))
                .andExpect(jsonPath("$.riskSubjects[0].subjectId")
                        .value(5));

        verify(academicResultService).getResultById(RESULT_ID);
    }

    // 5. GET /api/v1/students/{studentId}/results

    @Test
    void getStudentResults_shouldReturn200AndResultList()
            throws Exception {

        // Arrange
        AcademicResultResponse response =
                createAcademicResultResponse();

        when(academicResultService.getStudentResults(STUDENT_ID))
                .thenReturn(List.of(response));

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/students/{studentId}/results",
                        STUDENT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].semesterId").value(10))
                .andExpect(jsonPath("$[0].grade").value("B"));

        verify(academicResultService).getStudentResults(STUDENT_ID);
    }

    // 6. GET /api/v1/results/{resultId}/risk-subjects

    @Test
    void getRiskSubjects_shouldReturn200AndRiskSubjectList()
            throws Exception {

        // Arrange
        when(academicResultService.getRiskSubjects(RESULT_ID))
                .thenReturn(List.of(createRiskSubjectResponse()));

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/results/{resultId}/risk-subjects",
                        RESULT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].subjectId").value(5))
                .andExpect(jsonPath("$[0].subjectCode").value("CS102"))
                .andExpect(jsonPath("$[0].subjectName")
                        .value("Data Structures"))
                .andExpect(jsonPath("$[0].mark").value(35.00));

        verify(academicResultService).getRiskSubjects(RESULT_ID);
    }

    @Test
    void getRiskSubjects_whenEmpty_shouldReturn200AndEmptyList()
            throws Exception {

        // Arrange
        when(academicResultService.getRiskSubjects(RESULT_ID))
                .thenReturn(List.of());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/results/{resultId}/risk-subjects",
                        RESULT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(academicResultService).getRiskSubjects(RESULT_ID);
    }
}
