package com.princediana.airline_app.service;

import java.util.List;
import java.util.Map;

import com.princediana.airline_app.model.Airline;
import com.princediana.airline_app.model.Airport;
import com.princediana.airline_app.model.Flight;

public interface AirLabsService {
    List<Flight> getFlights(String depIata);
    List<Airport> getAirports(String country, String city);
    List<Airline> getAirlines();
    Map<String, String> getCountriesMap();
    Map<String, List<Map<String, Object>>> getCitiesByCountry();
}