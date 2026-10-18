package com.academic_survival_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "academic_results")
@Getter
@Setter
@NoArgsConstructor
public class AcademicResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "average_mark", precision = 5, scale = 2)
    private BigDecimal averageMark;

    @Column(length = 10)
    private String grade;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;
}

