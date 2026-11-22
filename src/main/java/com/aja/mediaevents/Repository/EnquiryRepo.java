package com.aja.mediaevents.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aja.mediaevents.Entity.EnquiryFormEntity;

public interface EnquiryRepo extends JpaRepository<EnquiryFormEntity, Long> 
{

}
