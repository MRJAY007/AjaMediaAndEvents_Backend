
package com.aja.mediaevents.Services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aja.mediaevents.Entity.AdminEntity;
import com.aja.mediaevents.Repository.AdminRepo;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(
            AdminRepo adminRepo,
            PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        String email = System.getenv("APP_ADMIN_EMAIL");
        String password = System.getenv("APP_ADMIN_PASSWORD");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            System.out.println("Initial admin credentials not configured.");
            return;
        }

        email = email.trim().toLowerCase();

        if (adminRepo.existsByEmail(email)) {
            System.out.println("Admin account already exists.");
            return;
        }

        AdminEntity admin = new AdminEntity();

        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);

        adminRepo.save(admin);

        System.out.println("Initial admin account created.");
    }
}
