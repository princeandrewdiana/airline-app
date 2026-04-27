package com.princediana.airline_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    private String name;
    private String iata;
    private String icao;

    private String city;
    private String countryCode;

    private Double lat;
    private Double lng;
}