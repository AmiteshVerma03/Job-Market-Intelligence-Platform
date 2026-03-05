package com.jobseeker.controller;

import com.jobseeker.entity.Job;
import com.jobseeker.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public Job createJob(@RequestBody Job job){
        return jobService.saveJob(job);
    }

    @GetMapping
    public List<Job> getJobs(){
        return jobService.getAllJobs();
    }
}