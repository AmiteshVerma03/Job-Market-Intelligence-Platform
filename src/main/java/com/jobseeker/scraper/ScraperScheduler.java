package com.jobseeker.scraper;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScraperScheduler {

    private final IndeedScraperService scraperService;

    @Scheduled(fixedRate = 3600000)
    public void runScraper() throws Exception {
        scraperService.scrapeJobs();
    }
}