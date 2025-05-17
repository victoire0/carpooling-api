package com.carpooling.api.dto;

import lombok.Data;

@Data
public class LocationDTO {
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String country;
} 