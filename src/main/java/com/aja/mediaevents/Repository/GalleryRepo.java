
package com.aja.mediaevents.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.mediaevents.Entity.GalleryEntity;

@Repository
public interface GalleryRepo extends JpaRepository<GalleryEntity, Long> {

    List<GalleryEntity> findAllByOrderByCreatedAtDesc();
}
