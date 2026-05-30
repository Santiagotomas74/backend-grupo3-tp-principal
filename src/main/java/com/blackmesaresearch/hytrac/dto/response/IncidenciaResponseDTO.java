package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record IncidenciaResponseDTO(

                Integer id,

                String ordenNumeroRemito,

                String usuarioRegistroLegajo,
                String usuarioGestionLegajo,

                String tipoIncidencia,

                String descripcion,

                LocalDateTime fechaIncidente,

                String leyAplicada,

                String accionesTomadas,

                Boolean resuelto

) {
}