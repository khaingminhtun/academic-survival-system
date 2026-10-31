package com.academic_survival_system;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.academic_survival_system.calculaton.AcademicCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcademicCalculatorTest {

    private AcademicCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new AcademicCalculator();
    }

    // Test 1: Grade A
    @Test
    void shouldReturnGradeA() {
        assertEquals(
                "A",
                calculator.calculateGrade(new BigDecimal("80"))
        );
    }

    // Test 2: Grade B
    @Test
    void shouldReturnGradeB() {
        assertEquals(
                "B",
                calculator.calculateGrade(new BigDecimal("75"))
        );
    }

    // Test 3: Grade C
    @Test
    void shouldReturnGradeC() {
        assertEquals(
                "C",
                calculator.calculateGrade(new BigDecimal("65"))
        );
    }

    // Test 4: Grade D
    @Test
    void shouldReturnGradeD() {
        assertEquals(
                "D",
                calculator.calculateGrade(new BigDecimal("45"))
        );
    }

    // Test 5: Grade F
    @Test
    void shouldReturnGradeF() {
        assertEquals(
                "F",
                calculator.calculateGrade(new BigDecimal("30"))
        );
    }

    // Test 6: HIGH risk
    @Test
    void shouldReturnHighRisk() {
        assertEquals(
                "HIGH",
                calculator.checkAcademicRisk(
                        new BigDecimal("30")
                )
        );
    }

    // Test 7: MEDIUM risk
    @Test
    void shouldReturnMediumRisk() {
        assertEquals(
                "MEDIUM",
                calculator.checkAcademicRisk(
                        new BigDecimal("50")
                )
        );
    }

    // Test 8: LOW risk
    @Test
    void shouldReturnLowRisk() {
        assertEquals(
                "LOW",
                calculator.checkAcademicRisk(
                        new BigDecimal("70")
                )
        );
    }

    // Test 9: Invalid mark
    @Test
    void shouldRejectMarkAbove100() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateGrade(
                        new BigDecimal("101")
                )
        );
    }

    // Test 10: Funny feedback for HIGH risk
    @Test
    void shouldGenerateHighRiskFeedback() {
        String feedback = calculator.generateFeedback(
                new BigDecimal("30"),
                "F",
                "HIGH"
        );

        assertNotNull(feedback);
        assertFalse(feedback.isBlank());
        assertTrue(feedback.contains("calculator"));
    }

    // Test 11: Feedback for excellent student
    @Test
    void shouldGenerateExcellentFeedback() {
        String feedback = calculator.generateFeedback(
                new BigDecimal("90"),
                "A",
                "LOW"
        );

        assertTrue(feedback.contains("superstar"));
    }

    // Test 12: Reject null feedback inputs
    @Test
    void shouldRejectNullFeedbackInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.generateFeedback(
                        null, "A", "LOW"
                )
        );
    }
}