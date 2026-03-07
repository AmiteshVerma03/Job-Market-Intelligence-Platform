package com.jobseeker.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jobseeker.entity.Job;
import com.jobseeker.entity.Skill;
import com.jobseeker.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JobRepository jobRepository;

    public Map<String, Integer> topSkills() {

        List<Job> jobs = jobRepository.findAll();

        Map<String, Integer> skillCount = new HashMap<>();

        for (Job job : jobs) {

            Set<Skill> skills = job.getSkills();

            if (skills == null)
                continue;

            for (Skill skill : skills) {
                String skillName = skill.getName();

                skillCount.put(skillName,
                        skillCount.getOrDefault(skillName, 0) + 1);
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

    public Map<String, Integer> topSkillsByLocation(String location) {

        List<Job> jobs = jobRepository.findByLocationIgnoreCase(location);

        Map<String, Integer> skillCount = new HashMap<>();

        for (Job job : jobs) {

            Set<Skill> skills = job.getSkills();

            if (skills == null)
                continue;

            for (Skill skill : skills) {

                String skillName = skill.getName();

                skillCount.put(skillName,
                        skillCount.getOrDefault(skillName, 0) + 1);
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

            Set<Skill> skills = job.getSkills();
            if (skills == null)
                continue;

            for (Skill s : skills) {
                if (s.getName().equalsIgnoreCase(skill)) {
                    if (job.getSalary() != null) {
                        totalSalary += job.getSalary();
                        count++;
                    }
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

    public Map<String, Double> averageSalaryByLocation() {

        List<Object[]> results = jobRepository.averageSalaryByLocation();

        Map<String, Double> response = new LinkedHashMap<>();

        for (Object[] row : results) {
            String location = (String) row[0];
            Double avgSalary = (Double) row[1];
            response.put(location, avgSalary);
        }

        return response;
    }
}
