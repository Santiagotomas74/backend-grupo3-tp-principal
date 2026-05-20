package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record OrdenCargaResponseDTO(

    Integer id,

    String numeroRemito,
    String cot,

    String estado,

    String camionPatente,
    String acopladoPatente,

    String transportistaNombre,
    String transportistaApellido,

    String operadorLegajo,

    String combustible,

    Double litrosCargados,

    LocalDateTime fechaCreacion,

    boolean confirmado

) {}