package com.blackmesaresearch.hytrac.dto.request;

public record EditarUsuarioRequestDTO(
    String nombre,
    String apellido,
    Long dni,
    String email,
    Integer rolId,
    Integer lugarOperativoId,
    Boolean activo,

    // si es transportista
    String cuit,
    Integer tipoVinculoId,
    Integer empresaId
) {}
