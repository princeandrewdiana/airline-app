package com.princediana.airline_app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.princediana.airline_app.model.Airline;
import com.princediana.airline_app.service.AirLabsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AirlineController {
	
	private final AirLabsService airLabsService;

	@GetMapping("/airlines")
	public String airlines(Model model) {
	    List<Airline> airlines = airLabsService.getAirlines();
	    model.addAttribute("airlines", airlines);
	    return "airlines";
	}

}
