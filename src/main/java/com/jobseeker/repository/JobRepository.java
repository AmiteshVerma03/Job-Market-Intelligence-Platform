package com.jobseeker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jobseeker.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

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

       Page<Job> findAll(Pageable pageable);
}