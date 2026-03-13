package com.yntec.idp.masters.service.service.serviceImpl;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String name = authentication.getName();
        if (name != null && !name.isBlank()) {
            try {
                return Optional.of(UUID.fromString(name));
            } catch (IllegalArgumentException ignored) {
                // Fall through to principal inspection for non-UUID names.
            }
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Jwt)) {
            return Optional.empty();
        }

        Jwt jwt = (Jwt) principal;

        return Optional.of(UUID.fromString(jwt.getSubject()));
    }
}
