package com.blackmesaresearch.hytrac.dto.response;

public record VehiculoResponseDTO(

    Integer id,
    String patente,
    String marca,
    String modelo,
    Double peso_maximo_carga
) {}