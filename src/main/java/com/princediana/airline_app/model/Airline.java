package com.princediana.airline_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airline {

    private String name;
    private String iata;
    private String icao;

    private String country;

    // Derived field (not from API directly)
    private String status; // Active / Inactive
}