package com.rental.app.controllers;

import com.rental.app.dtos.MessageDto;
import com.rental.app.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling message-related operations.
 * This controller manages the sending of messages within the application.
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Message management API")
@SecurityRequirement(name = "bearerAuth")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @Operation(summary = "Send a new message", description = "Creates a new message associated with a rental")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message sent successfully",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(type = "object",
                                                         example = "{\"message\": \"Message sent with success\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Rental or User not found")
    })
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        messageService.addMessage(messageDto);
        return ResponseEntity.ok().body("{\"message\": \"Message send with success\"}");
    }
}