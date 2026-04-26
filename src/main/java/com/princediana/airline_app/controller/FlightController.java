package com.princediana.airline_app.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.princediana.airline_app.model.Flight;
import com.princediana.airline_app.service.AirLabsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FlightController {

	private final AirLabsService airLabsService;

	@GetMapping("/flights")
	public String flights(@RequestParam(required = false) String depIata, Model model) {

	    List<Flight> flights = new ArrayList<>();

	    if (depIata != null && !depIata.isEmpty()) {
	        flights = airLabsService.getFlights(depIata);
	    }

	    model.addAttribute("flights", flights);
	    return "flights";
	}
}
