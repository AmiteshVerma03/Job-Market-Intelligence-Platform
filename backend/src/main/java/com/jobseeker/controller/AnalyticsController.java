package com.jobseeker.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobseeker.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // limit is now actually applied
    @GetMapping("/top-skills")
    public Map<String, Long> topSkills(@RequestParam(defaultValue = "10") int limit) {
        return analyticsService.topSkills(limit);
    }

    @GetMapping("/top-skills-by-location/{location}")
    public Map<String, Long> topSkillsByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "10") int limit) {
        return analyticsService.topSkillsByLocation(location, limit);
    }

    @GetMapping("/top-companies")
    public Map<String, Long> topCompanies() {
        return analyticsService.topCompanies();
    }

    @GetMapping("/salary-by-skill")
    public Map<String, Object> salaryBySkill(@RequestParam String skill) {
        double avgSalary = analyticsService.averageSalaryBySkill(skill);
        Map<String, Object> response = new HashMap<>();
        response.put("skill", skill);
        response.put("averageSalary", avgSalary);
        return response;
    }

    @GetMapping("/average-salary-by-location")
    public Map<String, Double> averageSalaryByLocation() {
        return analyticsService.averageSalaryByLocation();
    }
}