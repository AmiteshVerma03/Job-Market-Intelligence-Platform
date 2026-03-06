package com.jobseeker.repository;

import com.jobseeker.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

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
}