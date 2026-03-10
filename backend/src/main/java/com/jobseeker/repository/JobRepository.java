package com.jobseeker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jobseeker.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

       @EntityGraph(attributePaths = "skills")
       List<Job> findByLocationIgnoreCase(String location);

       @Query("""
                     SELECT j.company, COUNT(j)
                     FROM Job j
                     WHERE j.company IS NOT NULL
                     GROUP BY j.company
                     ORDER BY COUNT(j) DESC
                     """)
       List<Object[]> countJobsByCompany();

       @Query("""
                     SELECT j.location, AVG(j.salary)
                     FROM Job j
                     WHERE j.salary IS NOT NULL
                     GROUP BY j.location
                     ORDER BY AVG(j.salary) DESC
                     """)
       List<Object[]> averageSalaryByLocation();

       @EntityGraph(attributePaths = "skills")
       Page<Job> findAll(Pageable pageable);

       Optional<Job> findByUrl(String url);

       @EntityGraph(attributePaths = "skills")
       List<Job> findByCompanyIgnoreCase(String company);

       @EntityGraph(attributePaths = "skills")
       @Query("""
                     SELECT DISTINCT j
                     FROM Job j
                     JOIN j.skills s
                     WHERE LOWER(s.name) = LOWER(:skill)
                     """)
       List<Job> findJobsBySkill(String skill);

       // DB-level skill aggregation — avoids loading entire table into memory
       @Query("""
                     SELECT s.name, COUNT(j)
                     FROM Job j
                     JOIN j.skills s
                     GROUP BY s.name
                     ORDER BY COUNT(j) DESC
                     """)
       List<Object[]> countJobsBySkill(Pageable pageable);

       // DB-level skill aggregation filtered by location
       @Query("""
                     SELECT s.name, COUNT(j)
                     FROM Job j
                     JOIN j.skills s
                     WHERE LOWER(j.location) = LOWER(:location)
                     GROUP BY s.name
                     ORDER BY COUNT(j) DESC
                     """)
       List<Object[]> countJobsBySkillAndLocation(String location, Pageable pageable);

       // DB-level average salary for a specific skill
       @Query("""
                     SELECT AVG(j.salary)
                     FROM Job j
                     JOIN j.skills s
                     WHERE LOWER(s.name) = LOWER(:skill)
                     AND j.salary IS NOT NULL
                     """)
       Double averageSalaryBySkill(String skill);
}
