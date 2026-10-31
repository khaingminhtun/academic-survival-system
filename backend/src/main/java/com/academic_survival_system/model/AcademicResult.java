package com.academic_survival_system.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "academic_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(
            name = "average_mark",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal averageMark;

    @Column(nullable = false)
    private String grade;

    @Column(name = "feedback", nullable = false, columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @OneToMany(
            mappedBy = "academicResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<RiskSubject> riskSubjects = new ArrayList<>();
}


