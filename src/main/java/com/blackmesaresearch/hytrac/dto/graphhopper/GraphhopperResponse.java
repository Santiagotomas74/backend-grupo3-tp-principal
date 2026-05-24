package com.blackmesaresearch.hytrac.dto.graphhopper;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // Added import

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphhopperResponse(
        @JsonProperty("paths") List<GraphhopperRoute> routes) {
}