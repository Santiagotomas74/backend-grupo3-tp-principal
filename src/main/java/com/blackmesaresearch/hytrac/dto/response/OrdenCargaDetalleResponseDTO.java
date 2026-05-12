package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenCargaDetalleResponseDTO(

    Integer id,
    String numeroRemito,
    String cot,

    String estado,

    String camionPatente,
    String acopladoPatente,

    String transportista,
    String combustible,

    String plantaDespacho,
    String estacionDestino,

    Double litrosCargados,
    Double litrosEntregados,

    LocalDateTime fechaSalidaPlanta,
    LocalDateTime fechaEntregaEstimada,
    LocalDateTime fechaEntregaReal,

    String observaciones

) {}