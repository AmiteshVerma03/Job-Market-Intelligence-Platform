package com.jobseeker.controller;

import java.util.Map;

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

    // ── Task 1: all search endpoints return the same paged shape ───────────
    // Frontend can use totalPages / totalElements / page consistently

    @GetMapping
    public Map<String, Object> getJobs(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return toPageResponse(jobService.getAllJobs(page, size));
    }

    @GetMapping("/count")
    public Map<String, Long> getJobsCount() {
        return Map.of("count", jobService.countAllJobs());
    }

    // Task 1 — was List<Job>, now Page<Job>
    @GetMapping("/search/location")
    public Map<String, Object> getJobsByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return toPageResponse(jobService.findByLocation(location, page, size));
    }

    // Task 1 — was List<Job>, now Page<Job>
    @GetMapping("/search/company")
    public Map<String, Object> getJobsByCompany(
            @RequestParam String company,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return toPageResponse(jobService.findByCompany(company, page, size));
    }

    // Task 1 — was List<Job>, now Page<Job>
    @GetMapping("/search/skill")
    public Map<String, Object> getJobsBySkill(
            @RequestParam String skill,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return toPageResponse(jobService.findBySkill(skill, page, size));
    }

    // ── Helper: uniform page response shape ────────────────────────────────
    private Map<String, Object> toPageResponse(Page<Job> p) {
        return Map.of(
            "content",          p.getContent(),
            "page",             p.getNumber(),
            "size",             p.getSize(),
            "totalElements",    p.getTotalElements(),
            "totalPages",       p.getTotalPages(),
            "first",            p.isFirst(),
            "last",             p.isLast(),
            "numberOfElements", p.getNumberOfElements()
        );
    }
}

