package com.bacthinh.BacThinh.config.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private static final String SYSTEM_USER = "SYSTEM";
    private static final String ANONYMOUS_USER = "ANONYMOUS";

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of(ANONYMOUS_USER);
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                // getUsername() thực chất trả về email
                return Optional.of(userDetails.getUsername());
            }

            if (principal instanceof String username) {
                return Optional.of(username);
            }
            if (principal != null) {
                return Optional.of(principal.toString());
            }

            return Optional.of(SYSTEM_USER);

        } catch (Exception e) {
            log.warn("Error getting current auditor: {}", e.getMessage());
            return Optional.of(SYSTEM_USER);
        }
    }
}