package com.academic_survival_system.model;
import com.academic_survival_system.model.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "grades",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_grade_student_subject",
                        columnNames = {"student_id", "subject_id"}
                )
        }
)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal marks;

    @Column(name = "letter_grade", length = 2)
    private String letterGrade;
}