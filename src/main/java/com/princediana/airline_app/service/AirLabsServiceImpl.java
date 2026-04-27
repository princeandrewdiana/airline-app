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
    
    private Map<String, String> airlineCache = new HashMap<>();
    private Map<String, String> airportCache = new HashMap<>();
    private Map<String, String> countryCache = new HashMap<>();
    private Map<String, List<Map<String, Object>>> citiesByCountryCacheMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        loadAirlines();
        loadAirports();
        loadCities();
        loadCountries();
    }
    
    private void loadAirlines() {
        try {
            String url = baseUrl + "/airlines?api_key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            List<Map<String, Object>> data =
                    (List<Map<String, Object>>) response.getBody().get("response");

            for (Map<String, Object> item : data) {
                airlineCache.put(
                        (String) item.get("iata_code"),
                        (String) item.get("name")
                );
            }
        } catch (Exception ignored) {}
    }

    private void loadAirports() {
        try {
            String url = baseUrl + "/airports?api_key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            List<Map<String, Object>> data =
                    (List<Map<String, Object>>) response.getBody().get("response");

            for (Map<String, Object> item : data) {
                airportCache.put(
                        (String) item.get("iata_code"),
                        (String) item.get("name")
                );
            }
        } catch (Exception ignored) {}
    }
    
    private void loadCities() {
        try {
            String url = baseUrl + "/cities?api_key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            List<Map<String, Object>> data =
                    (List<Map<String, Object>>) response.getBody().get("response");

            for (Map<String, Object> item : data) {
            	
            	 String countryCode = (String) item.get("country_code");
            	 citiesByCountryCacheMap
                        .computeIfAbsent(countryCode, k -> new ArrayList<>())
                        .add(item);
                
            }
        } catch (Exception ignored) {}
    }
    
    private void loadCountries() {
    	
        try {
        	
            String url = baseUrl + "/countries?api_key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            List<Map<String, Object>> data =
                    (List<Map<String, Object>>) response.getBody().get("response");

            for (Map<String, Object> item : data) {
            	countryCache.put(
                        (String) item.get("code"),
                        (String) item.get("name")
                );
            }
        } catch (Exception ignored) {}
    }

    @Override
    public List<Flight> getFlights(String depIata) {
    	 String url = String.format(
                 "%s/flights?dep_iata=%s&api_key=%s",
                 baseUrl, depIata, apiKey
         );

         ResponseEntity<Map> response =
                 restTemplate.getForEntity(url, Map.class);

         if (response.getBody() == null) return List.of();

         Object raw = response.getBody().get("response");

         if (!(raw instanceof List<?> list)) return List.of();

         List<Map<String, Object>> data = (List<Map<String, Object>>) list;

         return data.stream()
                 .map(this::mapToFlight)
                 .toList();
    }

    @Override
    public List<Airport> getAirports(String country_code, String city_code) {

    	String url = String.format(
                "%s/airports?country_code=%s&city_code=%s&api_key=%s",
                baseUrl, country_code, city_code, apiKey
        );

        ResponseEntity<Map> response =
                restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null) return List.of();
        
        Object raw = response.getBody().get("response");

        if (!(raw instanceof List<?> list)) return List.of();
        
        List<Map<String, Object>> data = (List<Map<String, Object>>) list;

        return data.stream()
                .map(this::mapToAirport)
                .toList();
    }

    @Override
    public List<Airline> getAirlines() {

        String url = String.format(
                "%s/airlines?api_key=%s&_fields=name,iata_code,icao_code,country_code,is_scheduled",
                baseUrl,
                apiKey
        );

        ResponseEntity<Map> response =
                restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null || response.getBody().get("response") == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.getBody().get("response");

        return data.stream()
                .map(this::mapToAirline)
                .toList();
    }
    
    private Map<String, Object> getSchedule(String depIata, String arrIata, String flightIata) {

        try {
            String url = String.format(
                    "%s/schedules?dep_iata=%s&arr_iata=%s&flight_iata=%s&api_key=%s",
                    baseUrl, depIata, arrIata, flightIata, apiKey
            );

            ResponseEntity<Map> response =
                    restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null) return null;

            List<Map<String, Object>> data =
                    (List<Map<String, Object>>) response.getBody().get("response");

            return (data != null && !data.isEmpty()) ? data.get(0) : null;

        } catch (Exception e) {
            return null;
        }
    }
	
	private Flight mapToFlight(Map<String, Object> item) {
	
		String airlineIata = (String) item.get("airline_iata");
	    String depIata = (String) item.get("dep_iata");
	    String arrIata = (String) item.get("arr_iata");
	    String flightIata = (String) item.get("flight_iata");

	    // CALL SCHEDULES API
	    Map<String, Object> schedule =
	            getSchedule(depIata, arrIata, flightIata);

	    // =========================
	    // REAL DATA FROM SCHEDULES API
	    // =========================
	    String departureTime = "-";
	    String arrivalTime = "-";

	    if (schedule != null) {

	        // Departure time (BEST AVAILABLE FIELD)
	        departureTime = schedule.get("dep_estimated") != null
	                ? schedule.get("dep_estimated").toString()
	                : schedule.get("dep_time") != null
	                ? schedule.get("dep_time").toString()
	                : "-";

	        // Arrival time (BEST AVAILABLE FIELD)
	        arrivalTime = schedule.get("arr_estimated") != null
	                ? schedule.get("arr_estimated").toString()
	                : schedule.get("arr_time") != null
	                ? schedule.get("arr_time").toString()
	                : "-";
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

	            // REAL SCHEDULE TIMES
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
	            
	            // REAL COUNTRY NAME
	            .countryCode((String) countryCache.getOrDefault(countryCode, countryCode))

	            .lat(item.get("lat") != null ? ((Number) item.get("lat")).doubleValue() : 0.0)
	            .lng(item.get("lng") != null ? ((Number) item.get("lng")).doubleValue() : 0.0)
	            .build();
	}

	private Airline mapToAirline(Map<String, Object> item) {
		
		String name = (String) item.get("name");
		String iata_code = (String) item.get("iata_code");
		String icao_code = (String) item.get("icao_code");
		

	    return Airline.builder()
	            .name(name != null ? name : "-")
	            .iata(iata_code != null ? iata_code : "-")
	            .icao(icao_code != null ? icao_code : "-")
	            .build();
	}
	
	@Override
    public Map<String, String> getCountriesMap() {
    	return countryCache;
    }

	@Override
	public Map<String, List<Map<String, Object>>> getCitiesByCountry() {
		return citiesByCountryCacheMap;
	}
	
}