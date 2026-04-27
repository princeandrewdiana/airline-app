package com.princediana.airline_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City {
    private String name;
    private String city_code;
    private Double lat;
    private Double lng;
    private String country_code;
    private String type;
}
