package com.jobseeker.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import com.jobseeker.scraper.IndeedScraperService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test")
    public String test(Authentication authentication) {
        return "Hello " + authentication.getName();
    }

    private final IndeedScraperService scraperService;

    @GetMapping("/test-scraper")
    public ResponseEntity<Map<String, String>> runScraper() throws Exception {
        scraperService.scrapeJobs();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Scraper executed"));
    }

}
