package com.blackmesaresearch.hytrac.dto.response;

import java.util.List;

public record LoginResponseDTO(

        boolean success,
        String token,

        Integer id,
        String nombre,
        String apellido,
        String email,

        List<String> roles

) {
}