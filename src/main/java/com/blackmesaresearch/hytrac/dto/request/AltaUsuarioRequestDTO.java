package com.blackmesaresearch.hytrac.dto.request;

public record AltaUsuarioRequestDTO(
    String nombre,
    String apellido,
    Long dni,
    String email,
    String passwordTemporal,
    String rolNombre,
    Integer lugarOperativoId
) {}
