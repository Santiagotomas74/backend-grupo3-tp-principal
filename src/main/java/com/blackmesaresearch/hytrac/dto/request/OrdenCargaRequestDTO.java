package com.blackmesaresearch.hytrac.dto.request;

import java.time.LocalDateTime;

public record OrdenCargaRequestDTO(
     
     
        Integer camionId,

        Integer acopladoId,

        Integer transportistaId,

        Integer plantaDespachoId,

        Integer estacionDestinoId,

        Integer operadorId,

        Integer estadoId,

        Integer combustibleId,

        Integer rutaId,


        Double litrosCargados,

        Double litrosEntregados,


        // Valor total de la carga transportada
        Double valorMercaderia,


        LocalDateTime fechaCreacion,

        LocalDateTime fechaSalidaPlanta,

        LocalDateTime fechaEntrega,


        Double temperatura,

        String observaciones,

        boolean fieAdjunta,

        boolean confirmado) {
}