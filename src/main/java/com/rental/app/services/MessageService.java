package com.rental.app.services;

import com.rental.app.Utils.Mapper;
import com.rental.app.dtos.MessageDto;
import com.rental.app.entities.Message;
import com.rental.app.entities.Rental;
import com.rental.app.entities.User;
import com.rental.app.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service responsible for message-related operations.
 * This service handles the creation and storage of messages in the application.
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserInfoService userService;
    @Autowired
    private RentalService rentalService;

    public Message addMessage(MessageDto messageDto) {
        User user = userService.getUserById(messageDto.getUser_id());
        Rental rental = rentalService.getRentalById(messageDto.getRental_id());
        return messageRepository.save(Mapper.MapMessageDtoToMessage(messageDto, user, rental));
    }
}
