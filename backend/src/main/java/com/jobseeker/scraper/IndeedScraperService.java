package com.jobseeker.scraper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.jobseeker.entity.Job;
import com.jobseeker.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndeedScraperService {

    // Only depends on JobService — not directly on JobRepository (respects layered architecture)
    private final JobService jobService;

    public void scrapeJobs() throws Exception {

        List<String> keywords = List.of(
                "software engineer",
                "data scientist",
                "marketing",
                "sales",
                "accountant",
                "mechanical engineer",
                "civil engineer",
                "teacher",
                "nurse");

        for (String keyword : keywords) {

            for (int page = 0; page < 10; page++) {

                int start = page * 10;

                log.info("Scraping keyword: '{}' page: {}", keyword, page);

                // Proper URL encoding — handles special characters correctly
                String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
                String url = "https://www.indeed.com/jobs?q=" + encodedKeyword + "&start=" + start;

                Document doc;

                try {
                    doc = Jsoup.connect(url)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                            .header("Accept-Language", "en-US,en;q=0.9")
                            .header("Accept-Encoding", "gzip, deflate")
                            .header("Connection", "keep-alive")
                            .header("Upgrade-Insecure-Requests", "1")
                            .timeout(10000)
                            .get();

                } catch (Exception e) {
                    log.warn("Failed to fetch page: {} — reason: {}", url, e.getMessage());
                    continue;
                }

                Elements jobs = doc.select("div.job_seen_beacon");

                for (Element jobElement : jobs) {

                    String title = jobElement.select("h2.jobTitle").text();
                    String company = jobElement.select("span.companyName").text();
                    String location = jobElement.select("div.companyLocation").text();

                    String jobUrl = "https://www.indeed.com"
                            + jobElement.select("a").attr("href");

                    // Use service layer — not repository directly
                    if (jobService.existsByUrl(jobUrl)) {
                        continue;
                    }

                    Job job = new Job();
                    job.setTitle(title);
                    job.setCompany(company);
                    job.setLocation(location);
                    job.setUrl(jobUrl);

                    jobService.saveJob(job);
                }

                Thread.sleep(5000);
            }
        }

        log.info("Scraping completed.");
    }
}