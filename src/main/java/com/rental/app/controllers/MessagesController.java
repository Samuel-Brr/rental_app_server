package com.rental.app.controllers;

import com.rental.app.dtos.MessageDto;
import com.rental.app.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling message-related operations.
 * This controller manages the sending of messages within the application.
 */
@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        messageService.addMessage(messageDto);
        return ResponseEntity.ok().body("{\"message\": \"Message send with success\"}");
    }
}