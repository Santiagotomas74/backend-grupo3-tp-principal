package com.blackmesaresearch.hytrac.parser.impl;

import com.blackmesaresearch.hytrac.parser.DocumentParser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class VtvParser implements DocumentParser {
    private static final DateTimeFormatter VTV_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Map<String, Object> parse(String rawText) {
        Map<String, Object> parsedData = new LinkedHashMap<>();
        try {
            String[] lines = rawText.split("\n");
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }
                int colonIndex = trimmedLine.indexOf(":");
                if (colonIndex > 0) {
                    String key = trimmedLine.substring(0, colonIndex).trim().toLowerCase().replace(" ", "_");
                    String value = trimmedLine.substring(colonIndex + 1).trim();
                    
                    if ("vencimiento".equals(key)) {
                        parsedData.put(key, formatDate(value));
                    } else {
                        parsedData.put(key, value);
                    }
                }
            }
            if (parsedData.isEmpty()) {
                parsedData.put("error", "The payload format does not match the expected VTV structure.");
            }
        } catch (Exception e) {
            parsedData.put("error", "Failed to parse VTV layout: " + e.getMessage());
        }
        return parsedData;
    }

    private String formatDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate, VTV_DATE_FORMATTER).format(JSON_DATE_FORMATTER);
        } catch (Exception e) {
            return rawDate;
        }
    }
}
