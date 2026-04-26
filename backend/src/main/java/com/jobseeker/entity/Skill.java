package com.jobseeker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "skill",
    indexes = {
        // Task 4 — index on name: every analytics query does GROUP BY / JOIN on this column
        @Index(name = "idx_skill_name", columnList = "name", unique = true)
    }
)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
}