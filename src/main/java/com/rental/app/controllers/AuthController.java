package com.rental.app.controllers;

import com.rental.app.Utils.Mapper;
import com.rental.app.dtos.LoginDto;
import com.rental.app.dtos.RegisterDto;
import com.rental.app.entities.User;
import com.rental.app.services.JwtService;
import com.rental.app.services.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling authentication-related operations.
 * This includes user registration, login, and retrieving current user information.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management API")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserInfoService service;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully registered",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(type = "object",
                                                         example = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        User user = Mapper.MapRegisterDtoToUser(registerDto);
        service.addUser(user);
        String token = authenticateAndGetToken(Mapper.MapRegisterDtoToLoginDto(registerDto));
        return ResponseEntity.ok().body("{\"token\": " + token +"}");
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(type = "object",
                                                         example = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))
        ),
        @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok().body("{\"token\": " + jwtService.generateToken(authentication) +"}");
        } else {
            return ResponseEntity.status(401).body("{\"message\": \"error\"}");
        }
    }

    @Operation(summary = "Get current user", description = "Retrieves the details of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = User.class))
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token,
                                                  @RequestHeader HttpHeaders headers) {
        logger.info("Received request for /api/auth/me");
        logger.info("Authorization header: {}", token);

        // Log all request headers
        headers.forEach((key, value) -> logger.info("Header '{}': {}", key, value));

        try {
            if (token == null || token.isEmpty()) {
                logger.warn("Authorization token is missing");
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok().body(jwtService.getCurrentUser());
        } catch (Exception e) {
            logger.error("Error processing getCurrentUser request", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String authenticateAndGetToken(LoginDto  loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authentication);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}