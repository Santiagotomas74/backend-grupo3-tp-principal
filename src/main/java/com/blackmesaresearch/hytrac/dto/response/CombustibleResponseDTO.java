package com.blackmesaresearch.hytrac.dto.response;

public record CombustibleResponseDTO (
        Integer id,
        String nombre,
        String numeroOnu,
        String claseRiesgo,
        Double densidad,
        Double temperaturaReferencia

) {}