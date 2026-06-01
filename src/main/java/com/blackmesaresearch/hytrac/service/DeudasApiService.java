package com.blackmesaresearch.hytrac.service;


import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.blackmesaresearch.hytrac.dto.response.DeudasResponseDTO;

@Service
public class DeudasApiService {

    private final RestTemplate restTemplate;

    private final String API_URL = "https://api.bcra.gob.ar/centraldedeudores/v1.0/Deudas/";

    public DeudasApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Consulta Deuda Actual de CUIT
    public DeudasResponseDTO consultarDeudaActual(String cuit) {
        try {
        String urlCompleta = API_URL + cuit;

        ResponseEntity<DeudasResponseDTO> response = restTemplate.getForEntity(urlCompleta, DeudasResponseDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } 
        } catch (Exception e) {

            System.err.println("Error al consultar la API del BCRA para el CUIT " + cuit 
                                + " - Motivo: " + e.getMessage());
        }

        return null;

    }

    
}

