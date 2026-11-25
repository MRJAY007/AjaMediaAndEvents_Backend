
package com.aja.mediaevents.Controller;

import java.util.List;
import com.aja.mediaevents.dto.EnquiryResponseDTO;
import com.aja.mediaevents.Services.EnquiryService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import com.aja.mediaevents.dto.AdminEnquiryResponseDTO;
import com.aja.mediaevents.dto.AdminLoginDTO;
import com.aja.mediaevents.dto.AdminResponseDTO;
import com.aja.mediaevents.Services.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final EnquiryService enquiryService;
    
    public AdminController(
            AdminService adminService,
            EnquiryService enquiryService) {

        this.adminService = adminService;
        this.enquiryService = enquiryService;
    }

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();
    
    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok(csrfToken);
    }
    
    @GetMapping("/enquiries")
    public ResponseEntity<List<AdminEnquiryResponseDTO>> getAllEnquiries() {

        List<AdminEnquiryResponseDTO> enquiries =
        		enquiryService.getAllAdminEnquiries();

        return ResponseEntity.ok(enquiries);
    }
    
    @GetMapping("/enquiries/{id}")
    public ResponseEntity<AdminEnquiryResponseDTO> getEnquiryById(
            @PathVariable Long id) {

        AdminEnquiryResponseDTO enquiry =
                enquiryService.getAdminEnquiryById(id);

        return ResponseEntity.ok(enquiry);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AdminResponseDTO> login(
            @Valid @RequestBody AdminLoginDTO dto,
            HttpServletRequest request,
            HttpServletResponse response) {

        Authentication authentication = adminService.login(dto);
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context, request, response);

        AdminResponseDTO admin = new AdminResponseDTO(
                null,
                authentication.getName(),
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        return ResponseEntity.ok(admin);
    }

    @GetMapping("/me")
    public ResponseEntity<AdminResponseDTO> currentAdmin(
            Authentication authentication) {

        AdminResponseDTO admin = new AdminResponseDTO(
                null,
                authentication.getName(),
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        return ResponseEntity.ok(admin);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        new SecurityContextLogoutHandler()
                .logout(request, response, authentication);

        return ResponseEntity.ok("Logged out successfully");
        
        }
    }

