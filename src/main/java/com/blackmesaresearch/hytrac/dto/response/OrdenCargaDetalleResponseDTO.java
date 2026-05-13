package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenCargaDetalleResponseDTO(

    Integer id,
    String numeroRemito,
    String cot,

    String estado,

    String camionPatente,
    String acopladoPatente,

    // NUEVOS DATOS
    Double pesoMaximoCamion,
    Double capacidadTotalAcoplado,

    String transportista,
    String combustible,

    String plantaDespacho,
    String estacionDestino,

    Double litrosCargados,
    Double litrosEntregados,

    LocalDateTime fechaCreacion,
    LocalDateTime fechaSalidaPlanta,
    LocalDateTime fechaEntregaEstimada,
    LocalDateTime fechaEntregaReal,

    String observaciones,
    Boolean fieAdjunta,
    Boolean confirmado,

    // =========================
    // DATOS COMBUSTIBLE
    // =========================

    String combustibleNombre,
    String numeroOnu,
    String claseRiesgo,
    Double densidadReferencia,
    Double temperaturaReferencia

) {}