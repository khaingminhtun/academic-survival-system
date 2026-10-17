package com.academic_survival_system.model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_subject_code",
                        columnNames = "subject_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "credit_hours", nullable = false)
    private Integer creditHours;
}
