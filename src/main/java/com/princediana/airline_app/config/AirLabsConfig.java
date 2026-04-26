package com.princediana.airline_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AirLabsConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}