package com.princediana.airline_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private String flightNumber;
    private String airlineIata;
    private String airlineName;
    private String depIata;
    private String depAirportName;
    private String arrIata;
    private String arrAirportName;
    private String status;
    private String departureTime;
    private String arrivalTime;
}