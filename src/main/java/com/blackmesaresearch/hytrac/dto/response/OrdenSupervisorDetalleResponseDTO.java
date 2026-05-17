package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenSupervisorDetalleResponseDTO(

    // =========================
    // ORDEN
    // =========================

    Integer id,
    String numeroRemito,
    String cot,

    String estado,

    Boolean confirmado,
    Boolean fieAdjunta,

    String observaciones,

    // =========================
    // FECHAS
    // =========================

    LocalDateTime fechaCreacion,
    LocalDateTime fechaSalidaPlanta,
    LocalDateTime fechaEntregaEstimada,
    LocalDateTime fechaEntregaReal,

    // =========================
    // CARGA
    // =========================

    Double litrosCargados,
    Double litrosEntregados,

    Double temperaturaCarga,
    Double densidadCarga,

    // =========================
    // CAMION
    // =========================

    Integer camionId,
    String camionPatente,
    String camionMarca,
    String camionModelo,
    Double camionPesoMaximo,

    // =========================
    // ACOPLADO
    // =========================

    Integer acopladoId,
    String acopladoPatente,
    Double acopladoCapacidad,

    // =========================
    // TRANSPORTISTA
    // =========================

    Integer transportistaId,
    String transportistaNombre,
    String transportistaApellido,
    String transportistaCuit,
    String tipoVinculo,

    // =========================
    // COMBUSTIBLE
    // =========================

    Integer combustibleId,
    String combustibleNombre,
    String numeroOnu,
    String claseRiesgo,
    Double densidadReferencia,
    Double temperaturaReferencia,

    // =========================
    // LUGARES
    // =========================

    String plantaDespacho,
    String estacionDestino,

    // =========================
    // OPERADOR
    // =========================

    String operadorNombre

) {}