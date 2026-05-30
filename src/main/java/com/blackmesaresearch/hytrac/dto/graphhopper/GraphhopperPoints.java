package com.blackmesaresearch.hytrac.dto.graphhopper;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphhopperPoints(
                String type,
                List<List<Double>> coordinates) {
}