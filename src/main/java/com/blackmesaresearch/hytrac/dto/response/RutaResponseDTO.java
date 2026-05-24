package com.blackmesaresearch.hytrac.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;

public record RutaResponseDTO(
        Integer rutaId,
        Double distanciaKm,
        Double tiempoEstimadoHoras,
        Object geometria) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // A static helper method to turn your database string into a true JSON object
    public static Object parseGeometria(String jsonString) {
        try {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return null;
            }
            // readTree unpacks the escaped string into a clean JSON structure
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            return null;
        }
    }
}