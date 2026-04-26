package com.jobseeker.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Table(
    name = "job",
    indexes = {
        // Task 4 — indexes so WHERE/ORDER BY on these columns use index scans
        @Index(name = "idx_job_company",  columnList = "company"),
        @Index(name = "idx_job_location", columnList = "location"),
        @Index(name = "idx_job_url",      columnList = "url", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;
    private String description;
    private Integer salary;
    private String url;

    // Task 3 — LAZY fetch: skills only loaded when explicitly accessed.
    // Queries that need skills use @EntityGraph to JOIN in one query.
    // Analytics queries that never touch skills pay zero join cost.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_skills",
        joinColumns        = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;
}