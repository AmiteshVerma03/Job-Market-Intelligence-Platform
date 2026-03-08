package com.jobseeker.scraper;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.jobseeker.entity.Job;
import com.jobseeker.service.JobService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class IndeedScraperService {

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
                "nurse"
        );

        for (String keyword : keywords) {

            String url = "https://www.indeed.com/jobs?q="
                    + keyword.replace(" ", "+");

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            Elements jobs = doc.select("div.job_seen_beacon");

            for (Element jobElement : jobs) {

                String title = jobElement.select("h2.jobTitle").text();
                String company = jobElement.select("span.companyName").text();
                String location = jobElement.select("div.companyLocation").text();

                Job job = new Job();
                job.setTitle(title);
                job.setCompany(company);
                job.setLocation(location);

                jobService.saveJob(job);
            }

            Thread.sleep(2000); // avoid blocking
        }
    }
}