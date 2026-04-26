package com.princediana.airline_app.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Airline {
    private String name;
    private String iata;
    private String icao;
}