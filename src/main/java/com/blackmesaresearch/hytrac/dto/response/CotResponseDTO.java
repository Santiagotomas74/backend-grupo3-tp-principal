package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;


public record CotResponseDTO(

        String cot,

        String mensaje,

        LocalDateTime fechaGeneracion

) {}