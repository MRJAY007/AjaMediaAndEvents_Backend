
package com.aja.mediaevents.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.aja.mediaevents.dto.EnquiryRequestDTO;
import com.aja.mediaevents.dto.EnquiryResponseDTO;
import com.aja.mediaevents.Services.EnquiryService;

@RestController
@RequestMapping("/api/enquiries")
@CrossOrigin(origins = "http://localhost:3000")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @PostMapping
    public ResponseEntity<EnquiryResponseDTO> createEnquiry(
            @Valid @RequestBody EnquiryRequestDTO dto) {

        EnquiryResponseDTO response = enquiryService.saveEnquiry(dto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
