package com.rental.app.controllers;

import com.rental.app.dtos.RentalDto;
import com.rental.app.entities.Rental;
import com.rental.app.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling rental-related operations.
 * This controller manages the creation, retrieval, and updating of rental information.
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals", description = "Rental management API")
@SecurityRequirement(name = "bearerAuth")
public class RentalsController {

    @Autowired
    private RentalService rentalService;

    @Operation(summary = "Get all rentals", description = "Retrieves a list of all available rentals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of rentals",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(type = "array", implementation = Rental.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok().body(rentalService.getAllRentals());
    }

    @Operation(summary = "Get a rental by ID", description = "Retrieves a specific rental by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the rental",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Rental.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok().body(rentalService.getRentalById(id));
    }

    @Operation(summary = "Create a new rental", description = "Creates a new rental listing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental created successfully",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(type = "object",
                                                         example = "{\"message\": \"Rental created !\"}"))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRental(@ModelAttribute RentalDto rentalDto) {
        rentalService.addRental(rentalDto);
        return ResponseEntity.ok().body("{\"message\": \"Rental created !\"}");
    }

    @Operation(summary = "Update a rental", description = "Updates an existing rental by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental updated successfully",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(type = "object",
                                                         example = "{\"message\": \"Rental updated !\"}"))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRental(@PathVariable Long id, @ModelAttribute RentalDto rentalDto) {
        rentalService.updateRental(rentalDto, id);
        return ResponseEntity.ok().body("{\"message\": \"Rental updated !\"}");
    }
}
