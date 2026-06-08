package com.blackmesaresearch.hytrac.dto.response;

import java.util.List;

public record MetricasDashboardDTO(
    Integer totalEnvios,
    Integer enviosEnViaje,
    Integer enviosEntregados,
    Integer totalIncidencias,
    Double tasaEfectividad,
    List<EstadoMetricaDTO> desglosesPorEstado
) {}