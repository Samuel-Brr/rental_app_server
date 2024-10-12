package com.rental.app.dtos;

import lombok.Data;

@Data
public class MessageDto {
    private String message;
    private Long rental_id;
    private Long user_id;
}
