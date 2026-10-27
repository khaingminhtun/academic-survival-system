
package com.academic_survival_system.controller;

import com.academic_survival_system.dto.request.SemesterRequest;
import com.academic_survival_system.dto.response.SemesterResponse;
import com.academic_survival_system.service.SemesterService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

@WebMvcTest(SemesterController.class)
class SemesterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SemesterService semesterService;

    private static final Long STUDENT_ID = 1L;
    private static final Long SEMESTER_ID = 10L;

    private SemesterResponse createSemesterResponse() {
        return new SemesterResponse(
                SEMESTER_ID,
                STUDENT_ID,
                1,
                1
        );
    }

    private String validSemesterRequestJson() {
        return """
                {
                    "yearLevel": 1,
                    "semesterNumber": 1
                }
                """;
    }

    // 1. POST /api/v1/students/{studentId}/semesters

    @Test
    void createSemester_shouldReturn201Created() throws Exception {

        // Arrange
        when(semesterService.createSemester(
                eq(STUDENT_ID),
                any(SemesterRequest.class)
        )).thenReturn(createSemesterResponse());

        // Act and Assert
        mockMvc.perform(post(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSemesterRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.yearLevel").value(1))
                .andExpect(jsonPath("$.semesterNumber").value(1));

        verify(semesterService).createSemester(
                eq(STUDENT_ID),
                any(SemesterRequest.class)
        );
    }

    // 2. GET /api/v1/students/{studentId}/semesters

    @Test
    void getSemesters_withoutFilter_shouldReturn200AndList()
            throws Exception {

        // Arrange
        when(semesterService.getSemesters(STUDENT_ID, null))
                .thenReturn(List.of(createSemesterResponse()));

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].yearLevel").value(1))
                .andExpect(jsonPath("$[0].semesterNumber").value(1));

        verify(semesterService).getSemesters(STUDENT_ID, null);
    }

    @Test
    void getSemesters_withYearLevelFilter_shouldReturnFilteredList()
            throws Exception {

        // Arrange
        when(semesterService.getSemesters(STUDENT_ID, 2))
                .thenReturn(List.of(
                        new SemesterResponse(20L, STUDENT_ID, 2, 1),
                        new SemesterResponse(21L, STUDENT_ID, 2, 2)
                ));

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                )
                        .param("yearLevel", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].yearLevel").value(2))
                .andExpect(jsonPath("$[1].yearLevel").value(2))
                .andExpect(jsonPath("$[1].semesterNumber").value(2));

        verify(semesterService).getSemesters(STUDENT_ID, 2);
    }

    @Test
    void getSemesters_whenEmpty_shouldReturn200AndEmptyList()
            throws Exception {

        // Arrange
        when(semesterService.getSemesters(STUDENT_ID, null))
                .thenReturn(List.of());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(semesterService).getSemesters(STUDENT_ID, null);
    }

    // 3. GET /api/v1/students/{studentId}/semesters/{semesterId}

    @Test
    void getSemesterById_shouldReturn200AndSemester()
            throws Exception {

        // Arrange
        when(semesterService.getSemesterById(
                STUDENT_ID,
                SEMESTER_ID
        )).thenReturn(createSemesterResponse());

        // Act and Assert
        mockMvc.perform(get(
                        "/api/v1/students/{studentId}/semesters/{semesterId}",
                        STUDENT_ID,
                        SEMESTER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.yearLevel").value(1))
                .andExpect(jsonPath("$.semesterNumber").value(1));

        verify(semesterService).getSemesterById(
                STUDENT_ID,
                SEMESTER_ID
        );
    }

    // 4. PUT /api/v1/students/{studentId}/semesters/{semesterId}

    @Test
    void updateSemester_shouldReturn200AndUpdatedSemester()
            throws Exception {

        // Arrange
        String requestJson = """
                {
                    "yearLevel": 2,
                    "semesterNumber": 1
                }
                """;

        SemesterResponse response = new SemesterResponse(
                SEMESTER_ID,
                STUDENT_ID,
                2,
                1
        );

        when(semesterService.updateSemester(
                eq(STUDENT_ID),
                eq(SEMESTER_ID),
                any(SemesterRequest.class)
        )).thenReturn(response);

        // Act and Assert
        mockMvc.perform(put(
                        "/api/v1/students/{studentId}/semesters/{semesterId}",
                        STUDENT_ID,
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearLevel").value(2))
                .andExpect(jsonPath("$.semesterNumber").value(1));

        verify(semesterService).updateSemester(
                eq(STUDENT_ID),
                eq(SEMESTER_ID),
                any(SemesterRequest.class)
        );
    }

    // 5. DELETE /api/v1/students/{studentId}/semesters/{semesterId}

    @Test
    void deleteSemester_shouldReturn204NoContent()
            throws Exception {

        // Act and Assert
        mockMvc.perform(delete(
                        "/api/v1/students/{studentId}/semesters/{semesterId}",
                        STUDENT_ID,
                        SEMESTER_ID
                ))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(semesterService).deleteSemester(
                STUDENT_ID,
                SEMESTER_ID
        );
    }

    // 6. Validation: year level below minimum

    @Test
    void createSemester_withYearLevelZero_shouldReturn400()
            throws Exception {

        String requestJson = """
                {
                    "yearLevel": 0,
                    "semesterNumber": 1
                }
                """;

        mockMvc.perform(post(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(semesterService);
    }

    // 7. Validation: year level above maximum

    @Test
    void createSemester_withYearLevelAboveSix_shouldReturn400()
            throws Exception {

        String requestJson = """
                {
                    "yearLevel": 7,
                    "semesterNumber": 1
                }
                """;

        mockMvc.perform(post(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(semesterService);
    }

    // 8. Validation: semester number outside 1-2

    @Test
    void createSemester_withSemesterNumberThree_shouldReturn400()
            throws Exception {

        String requestJson = """
                {
                    "yearLevel": 1,
                    "semesterNumber": 3
                }
                """;

        mockMvc.perform(post(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(semesterService);
    }

    // 9. Validation: missing year level

    @Test
    void createSemester_withMissingYearLevel_shouldReturn400()
            throws Exception {

        String requestJson = """
                {
                    "yearLevel": null,
                    "semesterNumber": 1
                }
                """;

        mockMvc.perform(post(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(semesterService);
    }

    // 10. Validation: missing semester number

    @Test
    void createSemester_withMissingSemesterNumber_shouldReturn400()
            throws Exception {

        String requestJson = """
                {
                    "yearLevel": 1,
                    "semesterNumber": null
                }
                """;

        mockMvc.perform(post(
                        "/api/v1/students/{studentId}/semesters",
                        STUDENT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(semesterService);
    }

    // 11. Validation: invalid update request

    @Test
    void updateSemester_withInvalidYearLevel_shouldReturn400()
            throws Exception {

        String requestJson = """
                {
                    "yearLevel": 0,
                    "semesterNumber": 1
                }
                """;

        mockMvc.perform(put(
                        "/api/v1/students/{studentId}/semesters/{semesterId}",
                        STUDENT_ID,
                        SEMESTER_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(semesterService);
    }
}
