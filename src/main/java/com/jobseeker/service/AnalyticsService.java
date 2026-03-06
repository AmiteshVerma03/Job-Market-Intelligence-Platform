package com.jobseeker.service;

import com.jobseeker.entity.Job;
import com.jobseeker.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JobRepository jobRepository;

    public Map<String, Integer> topSkills() {

        List<Job> jobs = jobRepository.findAll();

        Map<String, Integer> skillCount = new HashMap<>();

        for (Job job : jobs) {

            String skills = job.getSkills();
            if (skills == null)
                continue;

            String[] skillArray = skills.split(",");

            for (String skill : skillArray) {
                skill = skill.trim();
                skillCount.put(skill, skillCount.getOrDefault(skill, 0) + 1);
            }
        }

        return skillCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new));
    }

    public Map<String, Integer> topSkillsByLocation(String location) {

        List<Job> jobs = jobRepository.findByLocationIgnoreCase(location);

        Map<String, Integer> skillCount = new HashMap<>();

        for (Job job : jobs) {

            String skills = job.getSkills();

            if (skills == null)
                continue;

            String[] skillArray = skills.split(",");

            for (String skill : skillArray) {

                skill = skill.trim();

                skillCount.put(skill,
                        skillCount.getOrDefault(skill, 0) + 1);
            }
        }

        return skillCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    public Map<String, Integer> topCompanies() {

        List<Job> jobs = jobRepository.findAll();

        Map<String, Integer> companyCount = new HashMap<>();

        for (Job job : jobs) {

            String company = job.getCompany();

            if (company == null)
                continue;

            companyCount.put(company,
                    companyCount.getOrDefault(company, 0) + 1);
        }

        return companyCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    public double averageSalaryBySkill(String skill) {

        List<Job> jobs = jobRepository.findAll();

        int totalSalary = 0;
        int count = 0;

        for (Job job : jobs) {

            String skills = job.getSkills();

            if (skills == null)
                continue;

            if (skills.toLowerCase().contains(skill.toLowerCase())) {
                if (job.getSalary() != null) {
                    totalSalary += job.getSalary();
                    count++;
                }
            }
        }

        if (count == 0)
            return 0;

        return (double) totalSalary / count;
    }

    public Map<String, Long> topCompaniesDB() {

        List<Object[]> results = jobRepository.countJobsByCompany();

        Map<String, Long> response = new LinkedHashMap<>();

        for (Object[] row : results) {
            String company = (String) row[0];
            Long count = (Long) row[1];
            response.put(company, count);
        }

        return response;
    }
}