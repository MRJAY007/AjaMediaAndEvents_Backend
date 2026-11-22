
package com.aja.mediaevents.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aja.mediaevents.dto.AdminEnquiryResponseDTO;
import com.aja.mediaevents.dto.EnquiryRequestDTO;
import com.aja.mediaevents.dto.EnquiryResponseDTO;
import com.aja.mediaevents.Entity.EnquiryFormEntity;
import com.aja.mediaevents.Repository.EnquiryRepo;

@Service
public class EnquiryService {

    private final EnquiryRepo enquiryRepo;

    public EnquiryService(EnquiryRepo enquiryRepo) {
        this.enquiryRepo = enquiryRepo;
    }

    // Save public enquiry
    public EnquiryResponseDTO saveEnquiry(EnquiryRequestDTO dto) {

        EnquiryFormEntity enquiry = new EnquiryFormEntity();

        enquiry.setName(dto.getName());
        enquiry.setPhone(dto.getPhone());
        enquiry.setOccasion(dto.getOccasion());
        enquiry.setGuests(dto.getGuests());
        enquiry.setDate(dto.getDate());
        enquiry.setLocation(dto.getLocation());

        EnquiryFormEntity saved = enquiryRepo.save(enquiry);

        return mapToResponse(saved);
    }

    // Get all enquiries for admin
    public List<EnquiryResponseDTO> getAllEnquiries() {

        List<EnquiryFormEntity> enquiries = enquiryRepo.findAll();

        List<EnquiryResponseDTO> responses = new ArrayList<>();

        for (EnquiryFormEntity enquiry : enquiries) {
            responses.add(mapToResponse(enquiry));
        }

        return responses;
    }

    // Convert entity to response DTO
    private EnquiryResponseDTO mapToResponse(EnquiryFormEntity enquiry) {

        EnquiryResponseDTO response = new EnquiryResponseDTO();

        response.setId(enquiry.getId());
        response.setName(enquiry.getName());
        response.setOccasion(enquiry.getOccasion());
        response.setGuests(enquiry.getGuests());
        response.setDate(enquiry.getDate());
        response.setStatus(enquiry.getStatus());
        response.setCreatedAt(enquiry.getCreatedAt());

        return response;
    }
    
 // Get all enquiries with customer details for admin
    public List<AdminEnquiryResponseDTO> getAllAdminEnquiries() {

        List<EnquiryFormEntity> enquiries = enquiryRepo.findAll();

        List<AdminEnquiryResponseDTO> responses = new ArrayList<>();

        for (EnquiryFormEntity enquiry : enquiries) {
            responses.add(mapToAdminResponse(enquiry));
        }

        return responses;
    }
    
 // Get single enquiry by ID for admin
    public AdminEnquiryResponseDTO getAdminEnquiryById(Long id) {

        EnquiryFormEntity enquiry = enquiryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Enquiry not found with ID: " + id));

        return mapToAdminResponse(enquiry);
    }
    
 // Convert entity to admin response DTO
    private AdminEnquiryResponseDTO mapToAdminResponse(EnquiryFormEntity enquiry) {

        AdminEnquiryResponseDTO response = new AdminEnquiryResponseDTO();

        response.setId(enquiry.getId());
        response.setName(enquiry.getName());
        response.setPhone(enquiry.getPhone());
        response.setOccasion(enquiry.getOccasion());
        response.setGuests(enquiry.getGuests());
        response.setDate(enquiry.getDate());
        response.setLocation(enquiry.getLocation());
        response.setStatus(enquiry.getStatus());
        response.setCreatedAt(enquiry.getCreatedAt());

        return response;
    }
    
}
