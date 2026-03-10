package com.jobseeker.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperScheduler {

    private final IndeedScraperService scraperService;

    @Scheduled(cron = "0 0 */6 * * *")
    public void runScraper() {
        log.info("Scheduled scraper starting...");
        try {
            scraperService.scrapeJobs();
            log.info("Scheduled scraper finished successfully.");
        } catch (Exception e) {
            // Log the failure — never silently swallow exceptions in scheduled tasks
            log.error("Scheduled scraper failed: {}", e.getMessage(), e);
        }
    }
}