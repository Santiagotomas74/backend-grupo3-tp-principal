package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record AuditoriaOrdenResponseDTO(

                String ordenNumeroRemito,

                String estadoAnteriorNombre,

                String estadoNuevoNombre,

                LocalDateTime fechaCambio,

                String solicitanteLegajo,

                String confirmadorLegajo,

                String motivo

) {
}