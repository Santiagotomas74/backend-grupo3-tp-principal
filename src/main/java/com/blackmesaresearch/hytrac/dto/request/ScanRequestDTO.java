package com.blackmesaresearch.hytrac.dto.request;

public class ScanRequestDTO {
    private String documentType; // "id", "drivers", "vehicle_verification", etc.
    private String base64Image;

    // Getters and Setters
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }
}
