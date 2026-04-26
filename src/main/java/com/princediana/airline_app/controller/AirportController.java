package com.princediana.airline_app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.princediana.airline_app.model.Airport;
import com.princediana.airline_app.service.AirLabsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AirportController {
	
	private final AirLabsService airLabsService;

	@GetMapping("/airports")
	public String airports(Model model) {
	    List<Airport> airports = airLabsService.getAirports();
	    model.addAttribute("airports", airports);
	    return "airports";
	}

}
