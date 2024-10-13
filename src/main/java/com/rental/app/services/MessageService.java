package com.rental.app.services;

import com.rental.app.utils.Mapper;
import com.rental.app.dtos.MessageDto;
import com.rental.app.entities.Message;
import com.rental.app.entities.Rental;
import com.rental.app.entities.User;
import com.rental.app.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for message-related operations.
 * This service handles the creation and storage of messages in the application.
 */
@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final UserInfoService userService;
    private final RentalService rentalService;

    public MessageService(MessageRepository messageRepository, UserInfoService userService, RentalService rentalService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.rentalService = rentalService;
    }

    /**
     * Adds a new message to the system.
     *
     * @param messageDto The DTO containing the message details.
     * @return The created Message entity.
     * @throws RuntimeException if the user or rental is not found.
     */
    @Transactional
    public Message addMessage(MessageDto messageDto) {
        logger.debug("Adding new message: {}", messageDto);

        User user = getUserForMessage(messageDto.getUser_id());
        Rental rental = getRentalForMessage(messageDto.getRental_id());

        Message message = Mapper.mapMessageDtoToMessage(messageDto, user, rental);
        Message savedMessage = messageRepository.save(message);

        logger.info("Message added successfully with ID: {}", savedMessage.getId());
        return savedMessage;
    }

    private User getUserForMessage(Long userId) {
        try {
            return userService.getUserById(userId);
        } catch (Exception e) {
            logger.error("User not found for message creation. User ID: {}", userId);
            throw new RuntimeException("User not found for message creation", e);
        }
    }

    private Rental getRentalForMessage(Long rentalId) {
        try {
            return rentalService.getRentalById(rentalId);
        } catch (Exception e) {
            logger.error("Rental not found for message creation. Rental ID: {}", rentalId);
            throw new RuntimeException("Rental not found for message creation", e);
        }
    }
}