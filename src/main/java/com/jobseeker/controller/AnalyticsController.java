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

    @GetMapping("/top-skills")
    public Map<String, Integer> topSkills(@RequestParam(defaultValue = "10") int limit) {
        return analyticsService.topSkills();// can add limit to show only top k-skills
    }

    @GetMapping("/top-skills-by-location/{location}")
    public Map<String, Integer> topSkillsByLocation(
            @PathVariable String location) {

        return analyticsService.topSkillsByLocation(location);
    }

    @GetMapping("/top-companies")
    public Map<String, Integer> topCompanies() {
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

    @GetMapping("/top-companies-db")
    public Map<String, Long> topCompaniesDB() {
        return analyticsService.topCompaniesDB();
    }
}