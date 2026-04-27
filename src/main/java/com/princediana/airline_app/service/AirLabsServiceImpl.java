package com.princediana.airline_app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.princediana.airline_app.model.Airline;
import com.princediana.airline_app.model.Airport;
import com.princediana.airline_app.model.Flight;

import jakarta.annotation.PostConstruct;
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

    // ===== CACHE =====
    private final Map<String, String> airlineCache = new HashMap<>();
    private final Map<String, String> airportCache = new HashMap<>();
    private final Map<String, String> countryCache = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> citiesByCountryCacheMap = new HashMap<>();

    // =========================
    // INIT
    // =========================
    @PostConstruct
    public void init() {
        loadAirlines();
        loadAirports();
        loadCountries();
        loadCities();
    }

    // =========================
    // GENERIC API CALL
    // =========================
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Map<String, Object>> fetchList(String url) {
        try {
            ResponseEntity<Map> response =
                    restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null) return List.of();

            Object raw = response.getBody().get("response");

            if (!(raw instanceof List<?> list)) return List.of();

            return (List<Map<String, Object>>) list;

        } catch (Exception e) {
            return List.of();
        }
    }

    private String buildUrl(String endpoint) {
        return String.format("%s%s?api_key=%s", baseUrl, endpoint, apiKey);
    }

    // =========================
    // LOADERS
    // =========================
    private void loadAirlines() {
        fetchList(buildUrl("/airlines")).forEach(item ->
                airlineCache.put(
                        (String) item.get("iata_code"),
                        (String) item.get("name")
                )
        );
    }

    private void loadAirports() {
        fetchList(buildUrl("/airports")).forEach(item ->
                airportCache.put(
                        (String) item.get("iata_code"),
                        (String) item.get("name")
                )
        );
    }

    private void loadCountries() {
        fetchList(buildUrl("/countries")).forEach(item ->
                countryCache.put(
                        (String) item.get("code"),
                        (String) item.get("name")
                )
        );
    }

    private void loadCities() {
        fetchList(buildUrl("/cities")).forEach(item -> {
            String countryCode = (String) item.get("country_code");

            citiesByCountryCacheMap
                    .computeIfAbsent(countryCode, k -> new ArrayList<>())
                    .add(item);
        });
    }

    // =========================
    // SERVICES
    // =========================
    @Override
    public List<Flight> getFlights(String depIata) {

        String url = String.format(
                "%s/flights?dep_iata=%s&api_key=%s",
                baseUrl, depIata, apiKey
        );

        List<Map<String, Object>> data = fetchList(url);

        return data.stream()
                .map(this::mapToFlight)
                .toList();
    }

    @Override
    public List<Airport> getAirports(String country, String city) {

        String url = String.format(
                "%s/airports?country_code=%s&city_code=%s&api_key=%s",
                baseUrl,
                country != null ? country : "",
                city != null ? city : "",
                apiKey
        );

        List<Map<String, Object>> data = fetchList(url);

        return data.stream()
                .map(this::mapToAirport)
                .toList();
    }

    @Override
    public List<Airline> getAirlines() {

        String url = String.format(
                "%s/airlines?api_key=%s&_fields=name,iata_code,icao_code,country_code,is_scheduled",
                baseUrl, apiKey
        );

        List<Map<String, Object>> data = fetchList(url);

        return data.stream()
                .map(this::mapToAirline)
                .toList();
    }

    // =========================
    // SCHEDULE API
    // =========================
    private Map<String, Object> getSchedule(String dep, String arr, String flight) {

        String url = String.format(
                "%s/schedules?dep_iata=%s&arr_iata=%s&flight_iata=%s&api_key=%s",
                baseUrl, dep, arr, flight, apiKey
        );

        List<Map<String, Object>> data = fetchList(url);

        return data.isEmpty() ? null : data.get(0);
    }

    // =========================
    // MAPPERS
    // =========================
    private Flight mapToFlight(Map<String, Object> item) {

        String airlineIata = (String) item.get("airline_iata");
        String depIata = (String) item.get("dep_iata");
        String arrIata = (String) item.get("arr_iata");
        String flightIata = (String) item.get("flight_iata");

        Map<String, Object> schedule =
                getSchedule(depIata, arrIata, flightIata);

        String departureTime = "-";
        String arrivalTime = "-";

        if (schedule != null) {
            departureTime = schedule.getOrDefault("dep_estimated",
                    schedule.getOrDefault("dep_time", "-")).toString();

            arrivalTime = schedule.getOrDefault("arr_estimated",
                    schedule.getOrDefault("arr_time", "-")).toString();
        }

        return Flight.builder()
                .flightNumber((String) item.get("flight_number"))
                .airlineIata(airlineIata)
                .airlineName(airlineCache.getOrDefault(airlineIata, airlineIata))
                .depIata(depIata)
                .depAirportName(airportCache.getOrDefault(depIata, depIata))
                .arrIata(arrIata)
                .arrAirportName(airportCache.getOrDefault(arrIata, arrIata))
                .status((String) item.get("status"))
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .build();
    }

    private Airport mapToAirport(Map<String, Object> item) {

        String countryCode = (String) item.get("country_code");

        return Airport.builder()
                .name((String) item.getOrDefault("name", "-"))
                .iata((String) item.getOrDefault("iata_code", "-"))
                .icao((String) item.getOrDefault("icao_code", "-"))
                .countryCode(countryCache.getOrDefault(countryCode, countryCode))
                .lat(toDouble(item.get("lat")))
                .lng(toDouble(item.get("lng")))
                .build();
    }

    private Airline mapToAirline(Map<String, Object> item) {

        return Airline.builder()
                .name((String) item.getOrDefault("name", "-"))
                .iata((String) item.getOrDefault("iata_code", "-"))
                .icao((String) item.getOrDefault("icao_code", "-"))
                .build();
    }

    private Double toDouble(Object value) {
        return value != null ? ((Number) value).doubleValue() : 0.0;
    }

    // =========================
    // GETTERS
    // =========================
    @Override
    public Map<String, String> getCountriesMap() {
        return countryCache;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getCitiesByCountry() {
        return citiesByCountryCacheMap;
    }
}