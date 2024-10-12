package com.rental.app.Utils;

import com.rental.app.dtos.LoginDto;
import com.rental.app.dtos.MessageDto;
import com.rental.app.dtos.RegisterDto;
import com.rental.app.dtos.RentalDto;
import com.rental.app.entities.Message;
import com.rental.app.entities.Rental;
import com.rental.app.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Utility class for mapping between DTOs and entity objects.
 * This class provides static methods to convert between different data representations.
 */
@Component
public class Mapper {

    public static User MapRegisterDtoToUser(RegisterDto registerDto) {
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        return user;
    }

    public static LoginDto MapRegisterDtoToLoginDto(RegisterDto registerDto) {
        LoginDto loginDto = new LoginDto();
        loginDto.setLogin(registerDto.getEmail());
        loginDto.setPassword(registerDto.getPassword());
        return loginDto;
    }

    public static Message MapMessageDtoToMessage(MessageDto messageDto, User user, Rental rental) {
        Message message = new Message();

        message.setMessage(messageDto.getMessage());
        message.setUser(user);
        message.setRental(rental);

        return message;
    }

    public static Rental MapRentalDtoToRental(RentalDto rentalDto, String pictureUrl) {
        Rental rental = new Rental();

        rental.setName(rentalDto.getName());
        rental.setSurface(new BigDecimal(rentalDto.getSurface()));
        rental.setPrice(new BigDecimal(rentalDto.getPrice()));
        rental.setPicture(pictureUrl);
        rental.setDescription(rentalDto.getDescription());
        return rental;
    }
}
