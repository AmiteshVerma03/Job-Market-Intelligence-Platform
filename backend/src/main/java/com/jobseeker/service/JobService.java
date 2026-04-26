package com.jobseeker.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobseeker.entity.Job;
import com.jobseeker.entity.Skill;
import com.jobseeker.repository.JobRepository;
import com.jobseeker.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    // @Transactional: skill saves + job save in one transaction — atomically safe
    @Transactional
    public Job saveJob(Job job) {
        Set<Skill> incomingSkills = job.getSkills();
        if (incomingSkills == null || incomingSkills.isEmpty()) {
            job.setSkills(new HashSet<>());
            return jobRepository.save(job);
        }
        String skillString = incomingSkills.stream()
                .map(Skill::getName)
                .collect(Collectors.joining(","));
        return saveJob(job, skillString);
    }

    @Transactional
    public Job saveJob(Job job, String skillString) {
        if (skillString == null || skillString.isBlank()) {
            job.setSkills(new HashSet<>());
            return jobRepository.save(job);
        }
        Set<Skill> skillSet = new HashSet<>();
        for (String skillName : skillString.split(",")) {
            String trimmed = skillName.trim();
            if (trimmed.isBlank()) continue;
            Skill skill = skillRepository.findByNameIgnoreCase(trimmed)
                    .orElseGet(() -> skillRepository.save(new Skill(null, trimmed)));
            skillSet.add(skill);
        }
        job.setSkills(skillSet);
        return jobRepository.save(job);
    }

    // URL check for scraper deduplication
    public boolean existsByUrl(String url) {
        return jobRepository.findByUrl(url).isPresent();
    }

    // ── Task 1: all paginated, consistent Page<Job> return type ────────────

    public Page<Job> getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return jobRepository.findAll(pageable);
    }

    // Task 1: location search now paginated
    public Page<Job> findByLocation(String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return jobRepository.findByLocationIgnoreCase(location, pageable);
    }

    // Task 1: company search now paginated
    public Page<Job> findByCompany(String company, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return jobRepository.findByCompanyIgnoreCase(company, pageable);
    }

    // Task 1: skill search now paginated
    public Page<Job> findBySkill(String skill, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return jobRepository.findJobsBySkill(skill, pageable);
    }

    public long countAllJobs() {
        return jobRepository.count();
    }
}

