package com.princediana.airline_app.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.princediana.airline_app.model.Airline;
import com.princediana.airline_app.model.Airport;
import com.princediana.airline_app.model.Flight;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class AirLabsServiceImpl implements AirLabsService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${airlabs.base-url}")
    private String baseUrl;

    @Value("${airlabs.api.key}")
    private String apiKey;

    @Override
    public List<Flight> getFlights(String depIata) {
        String url = String.format(
            "%s/flights?dep_iata=%s&api_key=%s",
            baseUrl, depIata, apiKey
        );

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("response");

        return data.stream().map(this::mapToFlight).toList();
    }

    @Override
    public List<Airport> getAirports() {
        String url = baseUrl + "/airports?api_key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("response");

        return data.stream().map(this::mapToAirport).toList();
    }

    @Override
    public List<Airline> getAirlines() {
        String url = baseUrl + "/airlines?api_key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("response");

        return data.stream().map(this::mapToAirline).toList();
    }

    // 🔽 Mapping methods

    private Flight mapToFlight(Map<String, Object> item) {
        return Flight.builder()
                .hex((String) item.get("hex"))
                .regNumber((String) item.get("reg_number"))
                .flag((String) item.get("flag"))

                .lat((Double) item.get("lat"))
                .lng((Double) item.get("lng"))
                .alt((item.get("alt") != null) ? ((Number) item.get("alt")).intValue() : null)

                .dir((item.get("dir") != null) ? ((Number) item.get("dir")).intValue() : null)
                .speed((item.get("speed") != null) ? ((Number) item.get("speed")).intValue() : null)
                .vSpeed((item.get("v_speed") != null) ? ((Number) item.get("v_speed")).intValue() : null)

                .flightNumber((String) item.get("flight_number"))
                .flightIcao((String) item.get("flight_icao"))
                .flightIata((String) item.get("flight_iata"))

                .depIcao((String) item.get("dep_icao"))
                .depIata((String) item.get("dep_iata"))

                .arrIcao((String) item.get("arr_icao"))
                .arrIata((String) item.get("arr_iata"))

                .airlineIcao((String) item.get("airline_icao"))
                .airlineIata((String) item.get("airline_iata"))

                .aircraftIcao((String) item.get("aircraft_icao"))

                .updated((item.get("updated") != null) ? ((Number) item.get("updated")).longValue() : null)

                .status((String) item.get("status"))
                .type((String) item.get("type"))
                .build();
    }
    
    private Airport mapToAirport(Map<String, Object> item) {
        return Airport.builder()
                .name((String) item.getOrDefault("name", "N/A"))
                .iata((String) item.getOrDefault("iata_code", "N/A"))
                .icao((String) item.getOrDefault("icao_code", "N/A"))
                .countryCode((String) item.getOrDefault("country_code", "N/A"))
                .lat(item.get("lat") != null ? ((Number) item.get("lat")).doubleValue() : 0.0)
                .lng(item.get("lng") != null ? ((Number) item.get("lng")).doubleValue() : 0.0)
                .build();
    }

    private Airline mapToAirline(Map<String, Object> item) {
        return Airline.builder()
                .name((String) item.getOrDefault("name", "N/A"))
                .iata(item.get("iata_code") != null ? item.get("iata_code").toString() : "N/A")
                .icao(item.get("icao_code") != null ? item.get("icao_code").toString() : "N/A")
                .build();
    }
    
}