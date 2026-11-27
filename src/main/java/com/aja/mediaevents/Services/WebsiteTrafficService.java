
package com.aja.mediaevents.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aja.mediaevents.Entity.WebsiteVisit;
import com.aja.mediaevents.Repository.WebsiteVisitRepository;

@Service
public class WebsiteTrafficService {

    private final WebsiteVisitRepository websiteVisitRepository;

    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");

    public WebsiteTrafficService(
            WebsiteVisitRepository websiteVisitRepository) {

        this.websiteVisitRepository = websiteVisitRepository;
    }

    // Record a website page visit
    public void recordVisit(String pagePath, String visitorId) {

        WebsiteVisit visit = new WebsiteVisit(pagePath, visitorId);

        websiteVisitRepository.save(visit);
    }

    // Get website traffic statistics
    public Map<String, Object> getTrafficStats() {

        Map<String, Object> stats = new HashMap<>();

        long totalVisits = websiteVisitRepository.count();

        long uniqueVisitors =
                websiteVisitRepository.countUniqueVisitors();

        LocalDate today = LocalDate.now(INDIA_ZONE);

        LocalDateTime startOfDay = today.atStartOfDay();

        LocalDateTime startOfTomorrow =
                today.plusDays(1).atStartOfDay();

        long todayVisits =
                websiteVisitRepository
                    .countByVisitedAtGreaterThanEqualAndVisitedAtLessThan(
                        startOfDay,
                        startOfTomorrow
                    );

        stats.put("totalVisits", totalVisits);
        stats.put("uniqueVisitors", uniqueVisitors);
        stats.put("todayVisits", todayVisits);

        return stats;
    }
}
