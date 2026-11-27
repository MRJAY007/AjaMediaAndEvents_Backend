
package com.aja.mediaevents.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Transactional
    @Query("DELETE FROM WebsiteVisit v WHERE v.visitedAt < :cutoff")
    int deleteVisitsOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
