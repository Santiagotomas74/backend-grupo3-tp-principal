package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenCargaResponseDTO(
        Integer id,
        String trackingId,

        String numeroRemito,
        String cot,

        String estado,
        String combustible,

        String plantaDespacho,
        String estacionDestino,

        Double litrosCargados,
        Double litrosEntregados,
        Integer ruta,

        LocalDateTime fechaCreacion,
        LocalDateTime fechaEntregaEstimada,

        String camionPatente,
        String acopladoPatente,

        String transportistaNombre,
        String transportistaApellido,
        String transportistaLegajo,

        String operadorLegajo,

        Boolean confirmado) {
}
