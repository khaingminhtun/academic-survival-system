package com.academic_survival_system.calculaton;


import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class AcademicCalculator {

    
    public void validateMark(BigDecimal mark) {
    if (mark == null) {
    throw new IllegalArgumentException("Mark cannot be null");
}

if (mark.compareTo(BigDecimal.ZERO) < 0
        || mark.compareTo(new BigDecimal("100")) > 0) {
    throw new IllegalArgumentException("Mark must be between 0 and 100");
 }
}
    
    public BigDecimal calculateAverage(List<BigDecimal> marks) {
        if (marks == null || marks.isEmpty()) {
    throw new IllegalArgumentException("Marks cannot be empty");
    }

    BigDecimal total = BigDecimal.ZERO;

for (BigDecimal mark : marks) {
    validateMark(mark);
    total = total.add(mark);
}

return total.divide(
    BigDecimal.valueOf(marks.size()),
    2,
    RoundingMode.HALF_UP
);
    }

    public String calculateGrade(BigDecimal average) {
        validateMark(average);

if (average.compareTo(new BigDecimal("80")) >= 0) {
    return "A";
} else if (average.compareTo(new BigDecimal("70")) >= 0) {
    return "B";
} else if (average.compareTo(new BigDecimal("60")) >= 0) {
    return "C";
} else if (average.compareTo(new BigDecimal("40")) >= 0) {
    return "D";
} else {
    return "F";
}
    }

    public String checkAcademicRisk(BigDecimal average) {
        if (average == null) {
    throw new IllegalArgumentException("Average cannot be null");
}

if (average.compareTo(new BigDecimal("40")) < 0) {
    return "HIGH";
} else if (average.compareTo(new BigDecimal("60")) < 0) {
    return "MEDIUM";
} else {
    return "LOW";
}
    }

    public String generateFeedback(
            BigDecimal average,
            String grade,
            String riskLevel
    ) {
        if (average == null || grade == null || riskLevel == null) {
    throw new IllegalArgumentException(
        "Average, grade, and risk level cannot be null"
    );
}

if (riskLevel.equals("HIGH")) {
    return "Your academic performance needs immediate improvement. Please seek help and study regularly.";
} else if (riskLevel.equals("MEDIUM")) {
    return "Your performance is fair. Keep practicing and focus on weak subjects.";
} else {
    return "Good job! Keep maintaining your academic performance.";
}
    }
}
