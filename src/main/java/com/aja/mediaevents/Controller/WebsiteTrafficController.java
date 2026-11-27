
package com.aja.mediaevents.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aja.mediaevents.Services.WebsiteTrafficService;
import com.aja.mediaevents.dto.TrafficTrackRequestDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class WebsiteTrafficController {

    private final WebsiteTrafficService websiteTrafficService;

    public WebsiteTrafficController(
            WebsiteTrafficService websiteTrafficService) {

        this.websiteTrafficService = websiteTrafficService;
    }

    // Public API: Record a page visit
    @PostMapping("/traffic/track")
    public ResponseEntity<String> trackVisit(
            @Valid @RequestBody TrafficTrackRequestDTO request) {

        websiteTrafficService.recordVisit(
                request.getPagePath(),
                request.getVisitorId()
        );

        return ResponseEntity.ok("Visit recorded successfully");
    }

    // Admin API: Get traffic statistics
    @GetMapping("/admin/traffic/stats")
    public ResponseEntity<Map<String, Object>> getTrafficStats() {

        return ResponseEntity.ok(
                websiteTrafficService.getTrafficStats()
        );
    }
}
