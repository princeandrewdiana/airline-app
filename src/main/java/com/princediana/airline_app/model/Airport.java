package com.princediana.airline_app.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Airport {
    private String name;
    private String iata;
    private String icao;
    private String countryCode;
    private Double lat;
    private Double lng;
}