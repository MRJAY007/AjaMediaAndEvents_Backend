
package com.aja.mediaevents.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aja.mediaevents.Entity.WebsiteVisit;

@Repository
public interface WebsiteVisitRepository
        extends JpaRepository<WebsiteVisit, Long> {

    @Query("SELECT COUNT(DISTINCT v.visitorId) FROM WebsiteVisit v")
    long countUniqueVisitors();

    long countByVisitedAtGreaterThanEqualAndVisitedAtLessThan(
            LocalDateTime start,
            LocalDateTime end
    );
}
