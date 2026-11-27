
package com.aja.mediaevents.Services;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.aja.mediaevents.Repository.WebsiteVisitRepository;

@Service
public class WebsiteVisitRetentionService {

    private static final Logger logger =
            LoggerFactory.getLogger(WebsiteVisitRetentionService.class);

    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final WebsiteVisitRepository websiteVisitRepository;

    public WebsiteVisitRetentionService(
            WebsiteVisitRepository websiteVisitRepository) {
        this.websiteVisitRepository = websiteVisitRepository;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Kolkata")
    public void deleteOldVisits() {

        LocalDateTime cutoff =
                LocalDateTime.now(INDIA_ZONE).minusDays(90);

        int deletedCount =
                websiteVisitRepository.deleteVisitsOlderThan(cutoff);

        if (deletedCount > 0) {
            logger.info(
                    "Deleted {} website visit records older than 90 days",
                    deletedCount
            );
        }
    }
}
