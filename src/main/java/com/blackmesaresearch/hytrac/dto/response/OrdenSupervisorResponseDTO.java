package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenSupervisorResponseDTO(

                Integer id,
                String trackingId,

                String numeroRemito,
                String cot,

                String estado,

                String camionPatente,
                String acopladoPatente,

                String transportista,

                String combustible,

                Double litrosCargados,
                Double litrosEntregados,

                String plantaDespacho,
                String estacionDestino,
                String observaciones,

                LocalDateTime fechaCreacion,
                LocalDateTime fechaEntregaEstimada,

                Boolean confirmado

) {
}