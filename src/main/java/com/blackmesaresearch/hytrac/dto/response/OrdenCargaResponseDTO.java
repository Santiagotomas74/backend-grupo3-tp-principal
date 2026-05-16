package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenCargaResponseDTO(
                Integer id,
                String numeroRemito,
                String cot,
                String estado,
                String combustibleTipo,
                String transportistaNombre,
                String plantaDespachoNombre,
                String estacionDestinoNombre,
                Double litrosCargados,
                LocalDateTime fechaCreacion,
                LocalDateTime fechaEntrega) {
}
