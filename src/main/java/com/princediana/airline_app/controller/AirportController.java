package com.princediana.airline_app.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.princediana.airline_app.model.Airport;
import com.princediana.airline_app.service.AirLabsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AirportController {
	
	private final AirLabsService airLabsService;

	@GetMapping("/airports")
	public String airports(
	        @RequestParam(required = false) String country,
	        @RequestParam(required = false) String city,
	        Model model) {
	
	    // AIRPORTS (FILTERED AT API LEVEL)
	    List<Airport> airports =
	            airLabsService.getAirports(country, city);
	
	    // COUNTRIES
	    Map<String, String> countryMap =
	            airLabsService.getCountriesMap();
	
	    List<Map.Entry<String, String>> countries =
	            new ArrayList<>(countryMap.entrySet());
	
	    // CITIES CACHE
	    Map<String, List<Map<String, Object>>> citiesByCountry =
	            airLabsService.getCitiesByCountry();
	
	    List<Map<String, Object>> cities =
	            citiesByCountry.getOrDefault(country, Collections.emptyList());
	
	    // RESOLVE SELECTED CITY OBJECT
	    Map<String, Object> selectedCityObj = null;
	
	    if (city != null && country != null) {
	        selectedCityObj = cities.stream()
	                .filter(c -> city.equals(c.get("city_code")))
	                .findFirst()
	                .orElse(null);
	    }
	
	    // MODEL
	    model.addAttribute("airports", airports);
	    model.addAttribute("countries", countries);
	    model.addAttribute("cities", cities);
	
	    model.addAttribute("selectedCountry", country);
	    model.addAttribute("selectedCity", city);
	    model.addAttribute("selectedCityObj", selectedCityObj);
	
	    return "airports";
	}

}
