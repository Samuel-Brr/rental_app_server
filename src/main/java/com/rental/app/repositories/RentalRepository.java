package com.rental.app.repositories;

import com.rental.app.entities.Rental;
import com.rental.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByOwner(User owner);

    List<Rental> findByPriceLessThanEqual(double maxPrice);
}
