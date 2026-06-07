package com.blackmesaresearch.hytrac.dto.request;

public record TransportistaOptimoRequestDTO(
    Double volumenCargaLitros,
    Double tiempoEfectivoEstimadoHoras,
    int combustibleId) {

}
