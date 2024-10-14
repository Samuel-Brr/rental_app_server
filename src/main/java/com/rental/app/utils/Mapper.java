package com.rental.app.utils;

import com.rental.app.dtos.LoginDto;
import com.rental.app.dtos.MessageDto;
import com.rental.app.dtos.RegisterDto;
import com.rental.app.dtos.CreateRentalDto;
import com.rental.app.entities.Message;
import com.rental.app.entities.Rental;
import com.rental.app.entities.User;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Utility class for mapping between DTOs and entity objects.
 * This class provides static methods to convert between different data representations.
 */
public final class Mapper {

    private Mapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Maps a RegisterDto to a User entity.
     *
     * @param registerDto The RegisterDto to map.
     * @return A new User entity.
     * @throws NullPointerException if registerDto is null.
     */
    public static User mapRegisterDtoToUser(RegisterDto registerDto) {
        Objects.requireNonNull(registerDto, "RegisterDto must not be null");
        return new User(registerDto.getName(), registerDto.getEmail(), registerDto.getPassword());
    }

    /**
     * Maps a RegisterDto to a LoginDto.
     *
     * @param registerDto The RegisterDto to map.
     * @return A new LoginDto.
     * @throws NullPointerException if registerDto is null.
     */
    public static LoginDto mapRegisterDtoToLoginDto(RegisterDto registerDto) {
        Objects.requireNonNull(registerDto, "RegisterDto must not be null");
        return new LoginDto(registerDto.getEmail(), registerDto.getPassword());
    }

    /**
     * Maps a MessageDto to a Message entity.
     *
     * @param messageDto The MessageDto to map.
     * @param user The User associated with the message.
     * @param rental The Rental associated with the message.
     * @return A new Message entity.
     * @throws NullPointerException if any parameter is null.
     */
    public static Message mapMessageDtoToMessage(MessageDto messageDto, User user, Rental rental) {
        Objects.requireNonNull(messageDto, "MessageDto must not be null");
        Objects.requireNonNull(user, "User must not be null");
        Objects.requireNonNull(rental, "Rental must not be null");
        return new Message(messageDto.getMessage(), user, rental);
    }

    /**
     * Maps a RentalDto to a Rental entity.
     *
     * @param createRentalDto The RentalDto to map.
     * @param pictureUrl The URL of the rental's picture.
     * @return A new Rental entity.
     * @throws NullPointerException if rentalDto is null.
     * @throws NumberFormatException if surface or price in rentalDto are not valid numbers.
     */
    public static Rental mapRentalDtoToRental(CreateRentalDto createRentalDto, String pictureUrl) {
        Objects.requireNonNull(createRentalDto, "RentalDto must not be null");
        Objects.requireNonNull(pictureUrl, "PictureUrl must not be null");
        return new Rental(
            createRentalDto.getName(),
            new BigDecimal(createRentalDto.getSurface()),
            new BigDecimal(createRentalDto.getPrice()),
            pictureUrl,
            createRentalDto.getDescription()
        );
    }
}
