
package com.aja.mediaevents.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aja.mediaevents.Entity.AdminEntity;

public interface AdminRepo extends JpaRepository<AdminEntity, Long> {

    Optional<AdminEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
