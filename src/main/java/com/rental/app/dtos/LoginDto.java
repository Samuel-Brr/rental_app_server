package com.rental.app.dtos;

import lombok.Data;

@Data
public class LoginDto {
    private String login;
    private String password;

    public LoginDto(String email, String password) {
        this.login = email;
        this.password = password;
    }
}
