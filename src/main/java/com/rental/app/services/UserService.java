package com.rental.app.services;

import com.rental.app.entities.User;
import com.rental.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user-related operations.
 * This service provides methods for retrieving user information.
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.getReferenceById(id);
    }
}
