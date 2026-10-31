package com.academic_survival_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "semesters",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_year_semester",
                        columnNames = {
                                "student_id",
                                "year_level",
                                "semester_number"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "year_level", nullable = false)
    @Min(1)
    @Max(6)
    private Integer yearLevel;

    @Column(name = "semester_number", nullable = false)
    @Min(1)
    @Max(2)
    private Integer semesterNumber;

    @OneToMany(
            mappedBy = "semester",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Subject> subjects = new ArrayList<>();

    @OneToMany(
            mappedBy = "semester",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<AcademicResult> results = new ArrayList<>();
}