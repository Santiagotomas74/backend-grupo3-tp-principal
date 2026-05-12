package com.blackmesaresearch.hytrac.dto.response;

public record LocalidadResponseDTO(
        Integer id,
        String nombre,
        String codigoPostal,
        Integer provinciaId,
        String provinciaNombre
) {}