package com.princediana.airline_app.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Flight {

    private String hex;
    private String regNumber;
    private String flag;

    private Double lat;
    private Double lng;
    private Integer alt;

    private Integer dir;
    private Integer speed;
    private Integer vSpeed;

    private String flightNumber;
    private String flightIcao;
    private String flightIata;

    private String depIcao;
    private String depIata;

    private String arrIcao;
    private String arrIata;

    private String airlineIcao;
    private String airlineIata;

    private String aircraftIcao;

    private Long updated;
    private String status;

    private String type;
}