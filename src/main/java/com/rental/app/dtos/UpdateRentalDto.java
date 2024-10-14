package com.rental.app.dtos;

import lombok.Data;

@Data
public class UpdateRentalDto {
    private String name;
    private String surface;
    private String price;
    private String description;
}
