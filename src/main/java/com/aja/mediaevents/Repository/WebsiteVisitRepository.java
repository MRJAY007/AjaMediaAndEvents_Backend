
package com.aja.mediaevents.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.mediaevents.Entity.WebsiteVisit;

@Repository
public interface WebsiteVisitRepository extends JpaRepository<WebsiteVisit, Long> {

    long countByVisitorId(String visitorId);

}
