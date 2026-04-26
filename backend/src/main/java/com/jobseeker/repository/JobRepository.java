package com.jobseeker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobseeker.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    // ── Paginated all-jobs (Task 1) ─────────────────────────────────────────
    // @EntityGraph joins skills in a single SQL query — no N+1
    @EntityGraph(attributePaths = "skills")
    Page<Job> findAll(Pageable pageable);

    // ── Paginated search by location (Task 1) ──────────────────────────────
    @EntityGraph(attributePaths = "skills")
    Page<Job> findByLocationIgnoreCase(String location, Pageable pageable);

    // ── Paginated search by company (Task 1) ───────────────────────────────
    @EntityGraph(attributePaths = "skills")
    Page<Job> findByCompanyIgnoreCase(String company, Pageable pageable);

    // ── Paginated search by skill (Task 1) ─────────────────────────────────
    // DISTINCT prevents duplicate rows from the JOIN
    @EntityGraph(attributePaths = "skills")
    @Query("""
        SELECT DISTINCT j
        FROM Job j
        JOIN j.skills s
        WHERE LOWER(s.name) = LOWER(:skill)
        """)
    Page<Job> findJobsBySkill(@Param("skill") String skill, Pageable pageable);

    // ── URL existence check (scraper deduplication) ─────────────────────────
    Optional<Job> findByUrl(String url);

    // ── Task 2: DB-level analytics — no Java-side loops ────────────────────

    // Top companies by job count
    @Query("""
        SELECT j.company, COUNT(j)
        FROM Job j
        WHERE j.company IS NOT NULL
        GROUP BY j.company
        ORDER BY COUNT(j) DESC
        """)
    List<Object[]> countJobsByCompany();

    // Average salary by location
    @Query("""
        SELECT j.location, AVG(j.salary)
        FROM Job j
        WHERE j.salary IS NOT NULL
        GROUP BY j.location
        ORDER BY AVG(j.salary) DESC
        """)
    List<Object[]> averageSalaryByLocation();

    // Top skills globally — Pageable provides the LIMIT (Task 2 + Task 1 limit param)
    @Query("""
        SELECT s.name, COUNT(j)
        FROM Job j
        JOIN j.skills s
        GROUP BY s.name
        ORDER BY COUNT(j) DESC
        """)
    List<Object[]> countJobsBySkill(Pageable pageable);

    // Top skills filtered by location
    @Query("""
        SELECT s.name, COUNT(j)
        FROM Job j
        JOIN j.skills s
        WHERE LOWER(j.location) = LOWER(:location)
        GROUP BY s.name
        ORDER BY COUNT(j) DESC
        """)
    List<Object[]> countJobsBySkillAndLocation(@Param("location") String location, Pageable pageable);

    // Average salary for one skill
    @Query("""
        SELECT AVG(j.salary)
        FROM Job j
        JOIN j.skills s
        WHERE LOWER(s.name) = LOWER(:skill)
        AND j.salary IS NOT NULL
        """)
    Double averageSalaryBySkill(@Param("skill") String skill);
}

