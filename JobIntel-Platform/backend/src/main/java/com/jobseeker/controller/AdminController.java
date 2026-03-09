package com.jobseeker.controller;

import com.jobseeker.scraper.IndeedScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IndeedScraperService scraperService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint(Authentication authentication) {
        return "Welcome Admin: " + authentication.getName();
    }

    // Scraper trigger is now protected — only ADMIN can call it
    @PostMapping("/run-scraper")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> runScraper() {
        try {
            scraperService.scrapeJobs();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Scraper executed successfully"));
        } catch (Exception e) {
            log.error("Manual scraper trigger failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Scraper failed: " + e.getMessage()));
        }
    }
}