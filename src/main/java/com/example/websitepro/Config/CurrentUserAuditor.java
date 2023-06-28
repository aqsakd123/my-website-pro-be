package com.example.websitepro.Config;

import com.example.websitepro.Entity.DTO.UserDTO;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class CurrentUserAuditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authen = SecurityContextHolder.getContext().getAuthentication();
        if (authen != null && authen.isAuthenticated()){
            return Optional.of(authen.getName());
        }
        return Optional.empty();
    }
}
