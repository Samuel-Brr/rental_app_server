package com.rental.app.controllers;

import com.rental.app.dtos.RentalDto;
import com.rental.app.entities.Rental;
import com.rental.app.services.RentalService;
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
public class RentalsController {

    @Autowired
    private RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok().body(rentalService.getAllRentals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok().body(rentalService.getRentalById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRental(@ModelAttribute RentalDto rentalDto) {
        rentalService.addRental(rentalDto);
        return ResponseEntity.ok().body("{\"message\": \"Rental created !\"}");
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRental(@PathVariable Long id, @ModelAttribute RentalDto rentalDto) {
        rentalService.updateRental(rentalDto, id);
        return ResponseEntity.ok().body("{\"message\": \"Rental updated !\"}");
    }
}
