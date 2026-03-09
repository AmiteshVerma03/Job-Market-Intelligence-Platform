package com.jobseeker.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // @Transactional ensures both skill saves and job save succeed together,
    // or both roll back — prevents orphaned skills in the DB
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

        String[] skillArray = skillString.split(",");

        Set<Skill> skillSet = new HashSet<>();

        for (String skillName : skillArray) {

            String trimmedSkillName = skillName.trim();
            if (trimmedSkillName.isBlank())
                continue;

            Skill skill = skillRepository
                    .findByNameIgnoreCase(trimmedSkillName)
                    .orElseGet(() -> skillRepository.save(
                            new Skill(null, trimmedSkillName)));

            skillSet.add(skill);
        }

        job.setSkills(skillSet);

        return jobRepository.save(job);
    }

    // Check URL existence without depending on scraper having a direct repo reference
    public boolean existsByUrl(String url) {
        return jobRepository.findByUrl(url).isPresent();
    }

    public Page<Job> getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage = jobRepository.findAll(pageable);
        jobsPage.getContent().forEach(this::initializeSkills);
        return jobsPage;
    }

    public long countAllJobs() {
        return jobRepository.count();
    }

    public List<Job> findByLocation(String location) {
        List<Job> jobs = jobRepository.findByLocationIgnoreCase(location);
        jobs.forEach(this::initializeSkills);
        return jobs;
    }

    public List<Job> findByCompany(String company) {
        List<Job> jobs = jobRepository.findByCompanyIgnoreCase(company);
        jobs.forEach(this::initializeSkills);
        return jobs;
    }

    public List<Job> findBySkill(String skill) {
        List<Job> jobs = jobRepository.findJobsBySkill(skill);
        jobs.forEach(this::initializeSkills);
        return jobs;
    }

    private void initializeSkills(Job job) {
        if (job.getSkills() != null) {
            job.getSkills().size();
        }
    }
}
