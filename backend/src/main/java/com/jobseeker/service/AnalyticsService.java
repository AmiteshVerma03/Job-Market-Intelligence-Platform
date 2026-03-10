package com.jobseeker.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.jobseeker.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JobRepository jobRepository;

    // Uses DB-level aggregation — no longer loads entire job table into memory
    public Map<String, Long> topSkills(int limit) {
        List<Object[]> results = jobRepository.countJobsBySkill(PageRequest.of(0, limit));
        Map<String, Long> response = new LinkedHashMap<>();
        for (Object[] row : results) {
            response.put((String) row[0], (Long) row[1]);
        }
        return response;
    }

    // Uses DB-level aggregation filtered by location
    public Map<String, Long> topSkillsByLocation(String location, int limit) {
        List<Object[]> results = jobRepository.countJobsBySkillAndLocation(location, PageRequest.of(0, limit));
        Map<String, Long> response = new LinkedHashMap<>();
        for (Object[] row : results) {
            response.put((String) row[0], (Long) row[1]);
        }
        return response;
    }

    // Uses DB-level aggregation
    public Map<String, Long> topCompanies() {
        List<Object[]> results = jobRepository.countJobsByCompany();
        Map<String, Long> response = new LinkedHashMap<>();
        for (Object[] row : results) {
            response.put((String) row[0], (Long) row[1]);
        }
        return response;
    }

    // Uses DB-level AVG() — no longer scans all jobs in Java
    public double averageSalaryBySkill(String skill) {
        Double avg = jobRepository.averageSalaryBySkill(skill);
        return avg != null ? avg : 0.0;
    }

    public Map<String, Double> averageSalaryByLocation() {
        List<Object[]> results = jobRepository.averageSalaryByLocation();
        Map<String, Double> response = new LinkedHashMap<>();
        for (Object[] row : results) {
            response.put((String) row[0], (Double) row[1]);
        }
        return response;
    }
}
