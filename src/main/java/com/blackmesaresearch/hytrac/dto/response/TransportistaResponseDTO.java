package com.blackmesaresearch.hytrac.dto.response;

public record TransportistaResponseDTO(

    Integer id,

    String nombre,
    String apellido,

    String cuit,

    String tipoVinculo

) {}