
package com.academic_survival_system.controller;

import com.academic_survival_system.dto.request.SubjectRequest;
import com.academic_survival_system.dto.response.SubjectResponse;
import com.academic_survival_system.service.SubjectService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubjectController.class)
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubjectService subjectService;

    private static final Long SEMESTER_ID = 10L;
    private static final Long SUBJECT_ID = 1L;

    private SubjectResponse createSubjectResponse() {
        return new SubjectResponse(
                SUBJECT_ID,
                SEMESTER_ID,
                "CS101",
                "Programming Fundamentals",
                new BigDecimal("85.50")
        );
    }

    private String validSubjectRequestJson() {
        return """
                [
                    {
                        "subjectId": null,
                        "subjectCode": "CS101",
                        "subjectName": "Programming Fundamentals",
                        "mark": 85.50
                    }
                ]
                """;
    }

    // 1. POST /api/v1/semesters/{semesterId}/subjects

    @Test
    void createSubjects_shouldReturn201Created() throws Exception {

        // Arrange
        SubjectResponse response = createSubjectResponse();

        when(subjectService.createSubjects(
                eq(SEMESTER_ID),
                anyList()
        )).thenReturn(List.of(response));

        // Act and Assert
        mockMvc.perform(post(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSubjectRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].semesterId").value(10))
                .andExpect(jsonPath("$[0].subjectCode").value("CS101"))
                .andExpect(jsonPath("$[0].subjectName")
                        .value("Programming Fundamentals"))
                .andExpect(jsonPath("$[0].mark").value(85.50));

        verify(subjectService).createSubjects(
                eq(SEMESTER_ID),
                anyList()
        );
    }

    // 2. GET /api/v1/semesters/{semesterId}/subjects

    @Test
    void getAllSubjects_shouldReturn200AndSubjectList() throws Exception {

        // Arrange
        when(subjectService.getAllSubjects(SEMESTER_ID))
                .thenReturn(List.of(createSubjectResponse()));

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].subjectCode").value("CS101"))
                .andExpect(jsonPath("$[0].mark").value(85.50));

        verify(subjectService).getAllSubjects(SEMESTER_ID);
    }

    @Test
    void getAllSubjects_whenEmpty_shouldReturn200AndEmptyList()
            throws Exception {

        // Arrange
        when(subjectService.getAllSubjects(SEMESTER_ID))
                .thenReturn(List.of());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(subjectService).getAllSubjects(SEMESTER_ID);
    }

    // 3. GET /api/v1/semesters/{semesterId}/subjects/{subjectId}

    @Test
    void getSubject_shouldReturn200AndSubject() throws Exception {

        // Arrange
        when(subjectService.getSubjectById(
                SEMESTER_ID,
                SUBJECT_ID
        )).thenReturn(createSubjectResponse());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/semesters/{semesterId}/subjects/{subjectId}",
                        SEMESTER_ID,
                        SUBJECT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.semesterId").value(10))
                .andExpect(jsonPath("$.subjectCode").value("CS101"))
                .andExpect(jsonPath("$.subjectName")
                        .value("Programming Fundamentals"));

        verify(subjectService).getSubjectById(
                SEMESTER_ID,
                SUBJECT_ID
        );
    }

    // 4. PUT /api/v1/semesters/{semesterId}/subjects/{subjectId}

    @Test
    void updateSubject_shouldReturn200AndUpdatedSubject()
            throws Exception {

        // Arrange
        String requestJson = """
                {
                    "subjectId": 1,
                    "subjectCode": "CS101",
                    "subjectName": "Advanced Programming",
                    "mark": 95.00
                }
                """;

        SubjectResponse response = new SubjectResponse(
                SUBJECT_ID,
                SEMESTER_ID,
                "CS101",
                "Advanced Programming",
                new BigDecimal("95.00")
        );

        when(subjectService.updateSubject(
                eq(SEMESTER_ID),
                eq(SUBJECT_ID),
                org.mockito.ArgumentMatchers.any(SubjectRequest.class)
        )).thenReturn(response);

        // Act and Assert
        mockMvc.perform(put(
                        "/api/v1/semesters/{semesterId}/subjects/{subjectId}",
                        SEMESTER_ID,
                        SUBJECT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectName")
                        .value("Advanced Programming"))
                .andExpect(jsonPath("$.mark").value(95.00));

        verify(subjectService).updateSubject(
                eq(SEMESTER_ID),
                eq(SUBJECT_ID),
                org.mockito.ArgumentMatchers.any(SubjectRequest.class)
        );
    }

    // 5. PUT /api/v1/semesters/{semesterId}/subjects/bulk

    @Test
    void updateSubjects_shouldReturn200AndUpdatedSubjectList()
            throws Exception {

        // Arrange
        String requestJson = """
                [
                    {
                        "subjectId": 1,
                        "subjectCode": "CS101",
                        "subjectName": "Advanced Programming",
                        "mark": 95.00
                    }
                ]
                """;

        SubjectResponse response = new SubjectResponse(
                SUBJECT_ID,
                SEMESTER_ID,
                "CS101",
                "Advanced Programming",
                new BigDecimal("95.00")
        );

        when(subjectService.updateSubjects(
                eq(SEMESTER_ID),
                anyList()
        )).thenReturn(List.of(response));

        // Act and Assert
        mockMvc.perform(put(
                        "/api/v1/semesters/{semesterId}/subjects/bulk",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].subjectName")
                        .value("Advanced Programming"))
                .andExpect(jsonPath("$[0].mark").value(95.00));

        verify(subjectService).updateSubjects(
                eq(SEMESTER_ID),
                anyList()
        );
    }

    // 6. DELETE /api/v1/semesters/{semesterId}/subjects/{subjectId}

    @Test
    void deleteSubject_shouldReturn204NoContent() throws Exception {

        // Act and Assert
        mockMvc.perform(delete(
                        "/api/v1/semesters/{semesterId}/subjects/{subjectId}",
                        SEMESTER_ID,
                        SUBJECT_ID
                ))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(subjectService).deleteSubject(
                SEMESTER_ID,
                SUBJECT_ID
        );
    }

    // 7. Validation: blank subject code

    @Test
    void createSubjects_withBlankSubjectCode_shouldReturn400()
            throws Exception {

        String requestJson = """
                [
                    {
                        "subjectId": null,
                        "subjectCode": "",
                        "subjectName": "Programming Fundamentals",
                        "mark": 85.50
                    }
                ]
                """;

        mockMvc.perform(post(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subjectService);
    }

    // 8. Validation: mark greater than 100

    @Test
    void createSubjects_withMarkAbove100_shouldReturn400()
            throws Exception {

        String requestJson = """
                [
                    {
                        "subjectId": null,
                        "subjectCode": "CS101",
                        "subjectName": "Programming Fundamentals",
                        "mark": 105.00
                    }
                ]
                """;

        mockMvc.perform(post(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subjectService);
    }

    // 9. Validation: negative mark

    @Test
    void createSubjects_withNegativeMark_shouldReturn400()
            throws Exception {

        String requestJson = """
                [
                    {
                        "subjectId": null,
                        "subjectCode": "CS101",
                        "subjectName": "Programming Fundamentals",
                        "mark": -1.00
                    }
                ]
                """;

        mockMvc.perform(post(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subjectService);
    }

    // 10. Validation: missing mark

    @Test
    void createSubjects_withMissingMark_shouldReturn400()
            throws Exception {

        String requestJson = """
                [
                    {
                        "subjectId": null,
                        "subjectCode": "CS101",
                        "subjectName": "Programming Fundamentals",
                        "mark": null
                    }
                ]
                """;

        mockMvc.perform(post(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subjectService);
    }

    // 11. Validation: negative subject ID

    @Test
    void createSubjects_withNegativeSubjectId_shouldReturn400()
            throws Exception {

        String requestJson = """
                [
                    {
                        "subjectId": -1,
                        "subjectCode": "CS101",
                        "subjectName": "Programming Fundamentals",
                        "mark": 85.50
                    }
                ]
                """;

        mockMvc.perform(post(
                        "/api/v1/semesters/{semesterId}/subjects",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subjectService);
    }

    // 12. Validation: blank subject name during single update

    @Test
    void updateSubject_withBlankSubjectName_shouldReturn400()
            throws Exception {

        String requestJson = """
                {
                    "subjectId": 1,
                    "subjectCode": "CS101",
                    "subjectName": "",
                    "mark": 85.50
                }
                """;

        mockMvc.perform(put(
                        "/api/v1/semesters/{semesterId}/subjects/{subjectId}",
                        SEMESTER_ID,
                        SUBJECT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subjectService);
    }

    // 13. Validation: invalid item in bulk update

    @Test
    void updateSubjects_withInvalidMark_shouldReturn400()
            throws Exception {

        String requestJson = """
                [
                    {
                        "subjectId": 1,
                        "subjectCode": "CS101",
                        "subjectName": "Programming Fundamentals",
                        "mark": 150.00
                    }
                ]
                """;

        mockMvc.perform(put(
                        "/api/v1/semesters/{semesterId}/subjects/bulk",
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subjectService);
    }
}
