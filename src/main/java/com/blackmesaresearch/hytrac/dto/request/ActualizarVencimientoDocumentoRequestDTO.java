package com.blackmesaresearch.hytrac.dto.request;

import java.time.LocalDate;

public record ActualizarVencimientoDocumentoRequestDTO(
        LocalDate fechaVencimiento
) {}