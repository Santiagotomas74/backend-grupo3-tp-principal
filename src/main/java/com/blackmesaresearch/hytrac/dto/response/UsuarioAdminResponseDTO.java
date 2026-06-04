package com.blackmesaresearch.hytrac.dto.response;

public record UsuarioAdminResponseDTO(

        Integer id,

        String nombre,
        String apellido,

        Long dni,

        String email,
        String legajo,

        String rol,

        String lugarOperativo,

        Boolean activo,

        String tipoVinculo,

        String cuit,

        String empresa

) {
}