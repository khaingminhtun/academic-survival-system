package com.academic_survival_system.model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "marks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_marks_student_subject",
                        columnNames = {"student_id", "subject_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "mark", nullable = false, precision = 5, scale = 2)
    private BigDecimal mark;
}

