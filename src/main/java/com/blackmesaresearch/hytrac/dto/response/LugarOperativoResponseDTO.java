package com.blackmesaresearch.hytrac.dto.response;

public record LugarOperativoResponseDTO(
                Integer id,
                String nombre,
                String direccion,
                Integer localidadId,
                String localidadNombre,
                Integer provinciaId,
                String provinciaNombre,
                Double latitud,
                Double longitud,
                Boolean puedeRecibir,
                Boolean puedeDespachar) {
}