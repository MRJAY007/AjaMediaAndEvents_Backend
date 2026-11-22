
package com.aja.mediaevents.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aja.mediaevents.Entity.WebsiteVisit;
import com.aja.mediaevents.Repository.WebsiteVisitRepository;

@Service
public class WebsiteTrafficService {

    private final WebsiteVisitRepository websiteVisitRepository;

    public WebsiteTrafficService(WebsiteVisitRepository websiteVisitRepository) {
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

        long uniqueVisitors = websiteVisitRepository.findAll()
                .stream()
                .map(WebsiteVisit::getVisitorId)
                .distinct()
                .count();

        LocalDate today = LocalDate.now();

        long todayVisits = websiteVisitRepository.findAll()
                .stream()
                .filter(visit -> visit.getVisitedAt().toLocalDate().equals(today))
                .count();

        stats.put("totalVisits", totalVisits);
        stats.put("uniqueVisitors", uniqueVisitors);
        stats.put("todayVisits", todayVisits);

        return stats;
    }
}
