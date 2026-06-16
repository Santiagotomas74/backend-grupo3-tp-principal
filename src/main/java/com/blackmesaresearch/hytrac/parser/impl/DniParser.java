package com.blackmesaresearch.hytrac.parser.impl;

import com.blackmesaresearch.hytrac.parser.DocumentParser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class DniParser implements DocumentParser {
    private static final DateTimeFormatter DNI_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Map<String, Object> parse(String rawText) {
        Map<String, Object> parsedData = new LinkedHashMap<>();
        try {
            String[] tokens = rawText.split("@");
            if (tokens.length >= 8) {
                parsedData.put("numero_tramite", tokens[0].trim());
                parsedData.put("apellidos", tokens[1].trim());
                parsedData.put("nombres", tokens[2].trim());
                parsedData.put("genero", tokens[3].trim());
                parsedData.put("dni", tokens[4].trim());
                parsedData.put("ejemplar", tokens[5].trim());
                parsedData.put("fecha_nacimiento", formatDate(tokens[6].trim()));
                parsedData.put("fecha_emision", formatDate(tokens[7].trim()));
            } else {
                parsedData.put("error", "The payload format does not match the expected DNI structure.");
            }
        } catch (Exception e) {
            parsedData.put("error", "Failed to parse DNI layout: " + e.getMessage());
        }
        return parsedData;
    }

    private String formatDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate, DNI_DATE_FORMATTER).format(JSON_DATE_FORMATTER);
        } catch (Exception e) {
            return rawDate;
        }
    }
}