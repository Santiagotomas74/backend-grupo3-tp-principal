package com.blackmesaresearch.hytrac.dto.response;

public record LoginResponseDTO(

                boolean success,
                String token,

                Integer id,
                String nombre,
                String apellido,
                String email,

                String rol

) {
}