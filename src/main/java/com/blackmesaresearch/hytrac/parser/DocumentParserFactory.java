package com.blackmesaresearch.hytrac.parser;

import com.blackmesaresearch.hytrac.parser.impl.DniParser;
import com.blackmesaresearch.hytrac.parser.impl.VtvParser;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DocumentParserFactory {

    private final Map<String, DocumentParser> parsers = new HashMap<>();

    public DocumentParserFactory() {
        // Register your document parsers here
        parsers.put("dni", new DniParser());
        parsers.put("vtv", new VtvParser());
        
        // Hook new documents here seamlessly:
        // parsers.put("vehicle_verification", new VehicleVerificationParser());
    }

    public DocumentParser getParser(String documentType) {
        if (documentType == null) {
            return parsers.get("generic");
        }
        
        // Returns the match, or falls back to a generic presentation if type doesn't exist
        return parsers.getOrDefault(documentType.toLowerCase().trim(), rawText -> {
            Map<String, Object> defaultMap = new HashMap<>();
            defaultMap.put("document_type_status", "Unrecognized document type configuration.");
            defaultMap.put("raw_payload", rawText);
            return defaultMap;
        });
    }
}