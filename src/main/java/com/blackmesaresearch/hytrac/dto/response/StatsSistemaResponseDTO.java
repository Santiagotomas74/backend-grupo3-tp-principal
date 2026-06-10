package com.blackmesaresearch.hytrac.dto.response;

public record StatsSistemaResponseDTO(

        int totalOrdenes,
        int totalEntregadas,
        int totalCanceladas,
        int totalEnCurso

) {

}
