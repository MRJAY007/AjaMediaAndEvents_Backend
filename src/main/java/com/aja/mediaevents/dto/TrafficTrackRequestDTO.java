
package com.aja.mediaevents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TrafficTrackRequestDTO {

    @NotBlank(message = "Page path is required")
    @Size(max = 255, message = "Page path cannot exceed 255 characters")
    private String pagePath;

    @NotBlank(message = "Visitor ID is required")
    @Size(max = 64, message = "Visitor ID cannot exceed 64 characters")
    private String visitorId;

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
}
