
package com.aja.mediaevents.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.aja.mediaevents.Entity.AdminEntity;
import com.aja.mediaevents.Repository.AdminRepo;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminDataInitializer.class);

    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${APP_ADMIN_PASSWORD:}")
    private String adminPassword;

    public AdminDataInitializer(
            AdminRepo adminRepo,
            PasswordEncoder passwordEncoder) {

        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        boolean emailConfigured = StringUtils.hasText(adminEmail);
        boolean passwordConfigured = StringUtils.hasText(adminPassword);

        if (!emailConfigured && !passwordConfigured) {
            logger.info(
                    "Initial admin credentials are not configured. Skipping admin initialization."
            );
            return;
        }

        if (!emailConfigured || !passwordConfigured) {
            throw new IllegalStateException(
                    "Both APP_ADMIN_EMAIL and APP_ADMIN_PASSWORD must be configured."
            );
        }

        String email = adminEmail.trim().toLowerCase();

        if (adminRepo.existsByEmail(email)) {
            logger.info("Initial admin account already exists.");
            return;
        }

        if (adminPassword.length() < 12) {
            throw new IllegalStateException(
                    "APP_ADMIN_PASSWORD must contain at least 12 characters."
            );
        }

        AdminEntity admin = new AdminEntity();

        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);

        adminRepo.save(admin);

        logger.info("Initial admin account created successfully.");
    }
}
