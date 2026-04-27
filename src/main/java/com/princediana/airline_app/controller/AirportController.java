package com.princediana.airline_app.controller;

import java.util.List;

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

        List<Airport> airports = airLabsService.getAirports();

        // 🔍 SERVER-SIDE FILTERING
        if (country != null && !country.isBlank()) {
            airports = airports.stream()
                    .filter(a -> a.getCountryCode() != null &&
                            a.getCountryCode().equalsIgnoreCase(country))
                    .toList();
        }

        if (city != null && !city.isBlank()) {
            airports = airports.stream()
                    .filter(a -> a.getCity() != null &&
                            a.getCity().toLowerCase().contains(city.toLowerCase()))
                    .toList();
        }

        model.addAttribute("airports", airports);
        model.addAttribute("country", country);
        model.addAttribute("city", city);

        return "airports";
    }

}
