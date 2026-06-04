package com.blackmesaresearch.hytrac.dto.response;

public record EmpresaTercerizadaResponseDTO (
    Integer id,
    String nombreFantasia,
    String razonSocial,
    String cuit,
    String direccion,
    String telefono,
    Boolean activo
){}
