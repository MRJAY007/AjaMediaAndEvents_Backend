
package com.aja.mediaevents.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aja.mediaevents.Services.WebsiteTrafficService;

@RestController
@RequestMapping("/api")
public class WebsiteTrafficController {

    private final WebsiteTrafficService websiteTrafficService;

    public WebsiteTrafficController(WebsiteTrafficService websiteTrafficService) {
        this.websiteTrafficService = websiteTrafficService;
    }

    // Public API: Record a page visit
    @PostMapping("/traffic/track")
    public ResponseEntity<String> trackVisit(@RequestBody Map<String, String> request) {

        String pagePath = request.get("pagePath");
        String visitorId = request.get("visitorId");

        if (pagePath == null || pagePath.isBlank()
                || visitorId == null || visitorId.isBlank()) {
            return ResponseEntity.badRequest().body("Page path and visitor ID are required");
        }

        websiteTrafficService.recordVisit(pagePath, visitorId);

        return ResponseEntity.ok("Visit recorded successfully");
    }

    // Admin API: Get traffic statistics
    @GetMapping("/admin/traffic/stats")
    public ResponseEntity<Map<String, Object>> getTrafficStats() {

        return ResponseEntity.ok(websiteTrafficService.getTrafficStats());
    }
}
