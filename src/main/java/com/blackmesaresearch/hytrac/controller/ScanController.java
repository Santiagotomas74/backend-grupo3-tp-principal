package com.blackmesaresearch.hytrac.controller;

import com.blackmesaresearch.hytrac.dto.request.ScanRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.ScanResponseDTO;
import com.blackmesaresearch.hytrac.service.ScanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scanner")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResponseDTO> scanBarcode(@RequestBody ScanRequestDTO request) {
        try {
            // Step 1: Decode the barcode based on the requested document type
            String rawText = scanService.decodeBarcode(request.getBase64Image(), request.getDocumentType());
            
            // Step 2: Parse payload based on doc type
            Map<String, Object> parsedData = scanService.parsePayload(rawText, request.getDocumentType());
            
            // Step 3: Respond
            return ResponseEntity.ok(new ScanResponseDTO("SUCCESS", rawText, parsedData));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ScanResponseDTO("ERROR: " + e.getMessage(), null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ScanResponseDTO("FAILED: " + e.getMessage(), null, null));
        }
    }
}
