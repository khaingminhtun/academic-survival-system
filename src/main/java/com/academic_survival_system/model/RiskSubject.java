package com.academic_survival_system.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "risk_subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_result_risk_subject",
                        columnNames = {"academic_result_id", "subject_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_result_id", nullable = false)
    private AcademicResult academicResult;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}