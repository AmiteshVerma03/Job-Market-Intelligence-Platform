package com.jobseeker.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<Job> getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepository.findAll(pageable);
    }

    public List<Job> findByLocation(String location) {
        return jobRepository.findByLocationIgnoreCase(location);
    }

    public List<Job> findByCompany(String company) {
        return jobRepository.findByCompanyIgnoreCase(company);
    }

    public List<Job> findBySkill(String skill) {
        return jobRepository.findJobsBySkill(skill);
    }
}
