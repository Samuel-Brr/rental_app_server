package com.rental.app.controllers;

import com.rental.app.dtos.MessageDto;
import com.rental.app.dtos.MessageRecord;
import com.rental.app.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling message-related operations.
 * This controller manages the sending of messages within the application.
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Message management API")
@SecurityRequirement(name = "Bearer Authentication")
@Validated
public class MessagesController {

    private static final Logger logger = LoggerFactory.getLogger(MessagesController.class);
    private static final String MESSAGE_SENT_SUCCESS = "Message sent with success";

    private final MessageService messageService;

    public MessagesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Send a new message", description = "Creates a new message associated with a rental")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message sent successfully",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = MessageRecord.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Rental or User not found")
    })
    @PostMapping
    public ResponseEntity<MessageRecord> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        try {
            messageService.addMessage(messageDto);
            logger.info("Message sent successfully: {}", messageDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new MessageRecord(MESSAGE_SENT_SUCCESS));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", messageDto, e);
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }
}