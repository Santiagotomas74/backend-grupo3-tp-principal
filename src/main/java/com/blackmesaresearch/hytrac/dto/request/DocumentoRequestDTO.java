package com.blackmesaresearch.hytrac.dto.request;

import java.time.LocalDate;

public record DocumentoRequestDTO (
    Integer tipoDocumentoId,
    String nroDocumento,
    LocalDate fechaEmision,
    LocalDate fechaVencimiento,
    String archivoUrl,
    Integer camionId,   // Opcional. Si el doc es del Camion.
    Integer acopladoId // Opcional. si el doc es del Acoplado.
) {}

