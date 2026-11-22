
package com.aja.mediaevents.Security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aja.mediaevents.Entity.AdminEntity;
import com.aja.mediaevents.Repository.AdminRepo;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminRepo adminRepo;

    public AdminUserDetailsService(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        AdminEntity admin = adminRepo.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Invalid credentials"));

        return User.withUsername(admin.getEmail())
                .password(admin.getPasswordHash())
                .authorities(admin.getRole())
                .disabled(!admin.isEnabled())
                .build();
    }
}
