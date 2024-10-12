package com.rental.app.services;

import com.rental.app.entities.User;
import com.rental.app.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service responsible for JWT (JSON Web Token) operations.
 * This service handles token generation and user retrieval based on JWT authentication.
 */
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    public JwtService(JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }

    /**
     * Retrieves the current authenticated user based on the JWT in the SecurityContext.
     *
     * @return The User entity of the currently authenticated user.
     * @throws IllegalStateException if no authentication is found in the SecurityContext.
     * @throws UsernameNotFoundException if the user corresponding to the JWT subject is not found.
     */
    public User getCurrentUser() {
        // Get the authentication object from the security context
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication found in SecurityContext");
        }

        // Get the JWT token
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // Extract the user identifier from the JWT claims
        String userEmail = jwt.getClaimAsString("sub"); // Assuming email is used as identifier

        // Find and return the user
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + userEmail));
    }
}
