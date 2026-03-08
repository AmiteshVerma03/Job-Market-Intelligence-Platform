package com.jobseeker.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobseeker.entity.Job;
import com.jobseeker.service.JobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public Job createJob(@RequestBody Job job) {
        return jobService.saveJob(job);
    }

    @GetMapping
    public Page<Job> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return jobService.getAllJobs(page, size);
    }

    @GetMapping("/search/location")
    public List<Job> getJobsByLocation(@RequestParam String location) {
        return jobService.findByLocation(location);
    }

    @GetMapping("/search/company")
    public List<Job> getJobsByCompany(@RequestParam String company) {
        return jobService.findByCompany(company);
    }

    @GetMapping("/search/skill")
    public List<Job> getJobsBySkill(@RequestParam String skill) {
        return jobService.findBySkill(skill);
    }
}