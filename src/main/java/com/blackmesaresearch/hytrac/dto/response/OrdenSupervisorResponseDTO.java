package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenSupervisorResponseDTO(

    Integer id,

    String numeroRemito,
    String cot,

    String estado,

    String camionPatente,
    String acopladoPatente,

    String transportista,

    String combustible,

    Double litrosCargados,

    String plantaDespacho,
    String estacionDestino,

    LocalDateTime fechaCreacion,
    LocalDateTime fechaEntregaEstimada,

    Boolean confirmado

) {}