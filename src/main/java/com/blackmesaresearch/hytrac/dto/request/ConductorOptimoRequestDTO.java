package com.blackmesaresearch.hytrac.dto.request;

public record ConductorOptimoRequestDTO(
    Double volumenCargaLitros,
    Double tiempoEfectivoEstimadoHoras,
    int combustibleId) {

}
