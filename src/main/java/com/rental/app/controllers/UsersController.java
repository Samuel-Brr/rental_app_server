package com.rental.app.controllers;

import com.rental.app.entities.User;
import com.rental.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling user-related operations.
 * This controller manages the retrieval of user information.
 */

@RestController
@RequestMapping("/api/user")
@Tag(name = "Users", description = "User management API")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user's details by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Attempting to retrieve user with ID: {}", id);
        try {
            User user = userService.getUserById(id);
            logger.info("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving user with ID: {}", id, e);
            throw new RuntimeException("An unexpected error occurred while retrieving the user", e);
        }
    }
}
