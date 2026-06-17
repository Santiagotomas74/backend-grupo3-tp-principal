package com.blackmesaresearch.hytrac.dto.request;


public record GenerarCotRequestDTO(

        String origen,

        String destino,

        Double litrosCargados,

        Double densidad,

        Double valorMercaderia,

        String producto,

        String numeroRemito

) {}