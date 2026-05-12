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
        Integer operadorId,
        Integer estadoId,
        Integer combustibleId,
        Double litrosCargados,
        Double litrosEntregados,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaSalidaPlanta,
        LocalDateTime fechaEntrega,
        Double temperatura,
        Double densidad,
        String observaciones,
        boolean fieAdjunta,
        boolean confirmado
) {}