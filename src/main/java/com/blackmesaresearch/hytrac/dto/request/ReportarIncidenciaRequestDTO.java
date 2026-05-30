package com.blackmesaresearch.hytrac.dto.request;

public record ReportarIncidenciaRequestDTO(

                String numeroRemito,

                String legajoTransportista,

                String tipoIncidencia,

                String descripcion

) {
}