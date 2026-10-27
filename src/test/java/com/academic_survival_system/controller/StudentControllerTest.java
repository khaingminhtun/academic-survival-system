package com.academic_survival_system.controller;


import com.academic_survival_system.dto.request.StudentRequest;
import com.academic_survival_system.dto.response.StudentResponse;
import com.academic_survival_system.service.StudentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void createStudent_shouldReturn201Created() throws Exception {

        // Arrange
        String requestJson = """
            {
                "studentId": "STU001",
                "fullName": "Aung Aung",
                "department": "IST"
            }
            """;

        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .studentId("STU001")
                .fullName("Aung Aung")
                .department("IST")
                .build();

        Mockito.when(studentService.createStudent(Mockito.any(StudentRequest.class)))
                .thenReturn(response);

        // Act and Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value("STU001"))
                .andExpect(jsonPath("$.fullName").value("Aung Aung"))
                .andExpect(jsonPath("$.department").value("IST"));

        Mockito.verify(studentService)
                .createStudent(Mockito.any(StudentRequest.class));
    }

    @Test
    void getAllStudents_shouldReturn200AndStudentList() throws Exception {

        // Arrange
        StudentResponse student = StudentResponse.builder()
                .id(1L)
                .studentId("STU001")
                .fullName("Aung Aung")
                .department("IST")
                .build();

        Mockito.when(studentService.getAllStudents())
                .thenReturn(List.of(student));

        // Act and Assert
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentId").value("STU001"));

        Mockito.verify(studentService).getAllStudents();
    }

    @Test
    void getStudentById_shouldReturn200AndStudent() throws Exception {

        // Arrange
        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .studentId("STU001")
                .fullName("Aung Aung")
                .department("IST")
                .build();

        Mockito.when(studentService.getStudentById(1L))
                .thenReturn(response);

        // Act and Assert
        mockMvc.perform(get("/api/v1/students/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Aung Aung"));

        Mockito.verify(studentService).getStudentById(1L);
    }

    @Test
    void updateStudent_shouldReturn200AndUpdatedStudent() throws Exception {

        // Arrange
        String requestJson = """
            {
                "studentId": "STU001",
                "fullName": "Aung Aung Updated",
                "department": "IST"
            }
            """;

        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .studentId("STU001")
                .fullName("Aung Aung Updated")
                .department("IST")
                .build();

        Mockito.when(
                studentService.updateStudent(
                        Mockito.eq(1L),
                        Mockito.any(StudentRequest.class)
                )
        ).thenReturn(response);

        // Act and Assert
        mockMvc.perform(put("/api/v1/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName")
                        .value("Aung Aung Updated"));

        Mockito.verify(studentService)
                .updateStudent(
                        Mockito.eq(1L),
                        Mockito.any(StudentRequest.class)
                );
    }

    @Test
    void deleteStudent_shouldReturn204NoContent() throws Exception {

        // Arrange
        Mockito.doNothing()
                .when(studentService)
                .deleteStudent(1L);

        // Act and Assert
        mockMvc.perform(delete("/api/v1/students/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        Mockito.verify(studentService).deleteStudent(1L);
    }

    @Test
    void createStudent_withBlankStudentId_shouldReturn400() throws Exception {

        // Arrange
        String requestJson = """
            {
                "studentId": "",
                "fullName": "Aung Aung",
                "department": "IST"
            }
            """;

        // Act and Assert
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        // Service should not be called for invalid input
        Mockito.verifyNoInteractions(studentService);
    }


}
