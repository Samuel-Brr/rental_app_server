package com.rental.app.controllers;

import com.rental.app.dtos.MessageRecord;
import com.rental.app.dtos.CreateRentalDto;
import com.rental.app.dtos.RentalsRecord;
import com.rental.app.dtos.UpdateRentalDto;
import com.rental.app.entities.Rental;
import com.rental.app.services.RentalService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controller responsible for handling rental-related operations.
 * This controller manages the creation, retrieval, and updating of rental information.
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals", description = "Rental management API")
@SecurityRequirement(name = "Bearer Authentication")
@Validated
public class RentalsController {

    private static final Logger logger = LoggerFactory.getLogger(RentalsController.class);
    private static final String RENTAL_CREATED = "Rental created !";
    private static final String RENTAL_UPDATED = "Rental updated !";
    @Value("${app.upload.dir:${user.home}}")
    private String uploadDir;

    private final RentalService rentalService;

    public RentalsController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Get all rentals", description = "Retrieves a list of all available rentals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of rentals",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = RentalsRecord.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<RentalsRecord> getAllRentals() {
        try {
            List<Rental> rentals = rentalService.getAllRentals();
            logger.info("Retrieved {} rentals", rentals.size());
            return ResponseEntity.ok(new RentalsRecord(rentals));
        } catch (Exception e) {
            logger.error("Error retrieving all rentals", e);
            throw new RuntimeException("An unexpected error occurred while retrieving rentals", e);
        }
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
        try {
            Rental rental = rentalService.getRentalById(id);
            logger.info("Retrieved rental with id: {}", id);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            logger.error("Error retrieving rental with id: {}", id, e);
            throw new RuntimeException("An unexpected error occurred while retrieving the rental", e);
        }
    }

    @Operation(summary = "Create a new rental", description = "Creates a new rental listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageRecord.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageRecord> createRental(@Valid @ModelAttribute CreateRentalDto createRentalDto) {
        try {
            rentalService.addRental(createRentalDto);
            logger.info("Created new rental: {}", createRentalDto);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageRecord(RENTAL_CREATED));
        } catch (Exception e) {
            logger.error("Error creating rental: {}", createRentalDto, e);
            throw new RuntimeException("An unexpected error occurred while creating the rental", e);
        }
    }

    @Operation(summary = "Update a rental", description = "Updates an existing rental by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageRecord.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageRecord> updateRental(@PathVariable Long id, @Valid @ModelAttribute UpdateRentalDto updateRentalDto) {
        try {
            rentalService.updateRental(updateRentalDto, id);
            logger.info("Updated rental with id: {}", id);
            return ResponseEntity.ok(new MessageRecord(RENTAL_UPDATED));
        } catch (Exception e) {
            logger.error("Error updating rental with id: {}", id, e);
            throw new RuntimeException("An unexpected error occurred while updating the rental", e);
        }
    }

    @Hidden
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}