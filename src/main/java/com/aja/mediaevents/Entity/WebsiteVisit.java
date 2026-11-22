
package com.aja.mediaevents.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "website_visits")
public class WebsiteVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String pagePath;

    @Column(nullable = false, length = 64)
    private String visitorId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime visitedAt;

    public WebsiteVisit() {
    }

    public WebsiteVisit(String pagePath, String visitorId) {
        this.pagePath = pagePath;
        this.visitorId = visitorId;
    }

    @PrePersist
    protected void onCreate() {
        this.visitedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public LocalDateTime getVisitedAt() {
        return visitedAt;
    }
}
