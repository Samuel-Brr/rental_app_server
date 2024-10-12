package com.rental.app.controllers;

import com.rental.app.Utils.Mapper;
import com.rental.app.dtos.LoginDto;
import com.rental.app.dtos.RegisterDto;
import com.rental.app.dtos.UserDto;
import com.rental.app.entities.User;
import com.rental.app.services.JwtService;
import com.rental.app.services.UserInfoService;
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
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserInfoService service;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        User user = Mapper.MapRegisterDtoToUser(registerDto);
        service.addUser(user);
        String token = authenticateAndGetToken(Mapper.MapRegisterDtoToLoginDto(registerDto));
        return ResponseEntity.ok().body("{\"token\": " + token +"}");
    }

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

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token,
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

            UserDto userDto = new UserDto(jwtService.getCurrentUser());
            logger.info("User retrieved successfully: {}", userDto);
            return ResponseEntity.ok().body(userDto);
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