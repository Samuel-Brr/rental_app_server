package com.rental.app.services;

import com.rental.app.entities.User;
import com.rental.app.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.expiration.hours}")
    private long jwtExpirationHours;

    public JwtService(JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    /**
     * Generates a JWT token for the given authentication.
     *
     * @param authentication The authentication object.
     * @return A JWT token as a string.
     */
    public String generateToken(Authentication authentication) {
        logger.debug("Generating token for user: {}", authentication.getName());
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plus(jwtExpirationHours, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        String token = this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        logger.info("Token generated successfully for user: {}", authentication.getName());
        return token;
    }

    /**
     * Retrieves the current authenticated user based on the JWT in the SecurityContext.
     *
     * @return The User entity of the currently authenticated user.
     * @throws RuntimeException if no authentication is found in the SecurityContext.
     * @throws UsernameNotFoundException if the user corresponding to the JWT subject is not found.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            logger.error("No JWT authentication found in SecurityContext");
            throw new RuntimeException("No JWT authentication found");
        }

        Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();

        String userEmail = jwt.getSubject();
        logger.debug("Retrieving user for email: {}", userEmail);

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", userEmail);
                    return new UsernameNotFoundException("User not found for email: " + userEmail);
                });
    }
}