package com.rental.app.dtos;

import com.rental.app.entities.Rental;

import java.util.List;

public record RentalsRecord(List<Rental> rentals) {
}
