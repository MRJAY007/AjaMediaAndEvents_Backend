
package com.aja.mediaevents.Services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.aja.mediaevents.dto.AdminLoginDTO;

@Service
public class AdminService {

    private final AuthenticationManager authenticationManager;

    public AdminService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Authentication login(AdminLoginDTO dto) {

        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.getEmail().trim().toLowerCase(),
                dto.getPassword()
            )
        );
    }
}
