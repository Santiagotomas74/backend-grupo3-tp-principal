package com.blackmesaresearch.hytrac.dto.request;

public record CancelarOrdenRequestDTO(
        Integer solicitanteId,
        String motivo
) {}