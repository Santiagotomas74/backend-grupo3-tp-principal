package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenTransportistaResponseDTO(

        Integer id,

        String numeroRemito,
        String cot,

        String estado,

        String camionPatente,
        String acopladoPatente,

        String combustible,

        Double litrosCargados,

        String plantaDespacho,
        String estacionDestino,

        LocalDateTime fechaEntregaEstimada,

        Boolean confirmado

) {
}