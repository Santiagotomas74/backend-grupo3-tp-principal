package com.blackmesaresearch.hytrac.dto.graphhopper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphhopperRoute(
        Double distance,
        Long time,
        GraphhopperPoints points) {
}