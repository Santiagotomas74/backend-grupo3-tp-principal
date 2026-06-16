package com.blackmesaresearch.hytrac.dto.response;

import java.util.Map;

public class ScanResponseDTO {
    private String status;
    private String rawText;
    private Map<String, Object> parsedPayload;

    public ScanResponseDTO(String status, String rawText, Map<String, Object> parsedPayload) {
        this.status = status;
        this.rawText = rawText;
        this.parsedPayload = parsedPayload;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public String getRawText() { return rawText; }
    public Map<String, Object> getParsedPayload() { return parsedPayload; }
}
