package com.rental.app.dtos;

import lombok.Data;


/**
 * Dto class to send back the jwt token
 */
@Data
public class TokenDto {
    private String token;

    public TokenDto(String token) {
        this.token = token;
    }
}
