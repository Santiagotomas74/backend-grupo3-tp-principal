package com.blackmesaresearch.hytrac.dto.request;

import java.time.LocalDateTime;

public record OrdenCargaRequestDTO(
        String numeroRemito,
        String cot,
        Integer camionId,
        Integer acopladoId,
        Integer transportistaId,
        Integer plantaDespachoId,
        Integer estacionDestinoId,
        Integer combustibleId,
        Double litrosCargados,
        LocalDateTime fechaEntrega,
        String observaciones
) {}