package com.rental.app.repositories;

import com.rental.app.entities.Message;
import com.rental.app.entities.Rental;
import com.rental.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRental(Rental rental);

    List<Message> findByUser(User user);
}
