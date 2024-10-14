package com.rental.app.services;

import com.rental.app.entities.User;
import com.rental.app.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user-related operations and authentication.
 * This service implements Spring Security's UserDetailsService interface
 * and provides methods for user authentication, creation, and retrieval.
 */
@Service("userDetailsService")
public class UserInfoService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInfoService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads a user by username (email in this case) for authentication purposes.
     *
     * @param username The username (email) of the user to load.
     * @return UserDetails object containing the user's information.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by username: {}", username);
        return userRepository.findByEmail(username)
                .map(UserInfoDetails::new)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }

    /**
     * Adds a new user to the system.
     *
     * @param user The user to add.
     * @return The ID of the newly created user.
     * @throws RuntimeException if a user with the same email already exists.
     */
    @Transactional
    public Long addUser(User user) {
        logger.debug("Attempting to add new user: {}", user.getEmail());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Attempt to add user with existing email: {}", user.getEmail());
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User added successfully: {}", savedUser.getId());
        return savedUser.getId();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The User entity.
     * @throws RuntimeException if the user is not found.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        logger.debug("Attempting to get user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
    }
}