package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDate;

public record DocumentoResponseDTO(
    Integer id,
    String tipoDocumentoNombre,
    String nroDocumento,
    LocalDate fechaEmision,
    LocalDate fechaVencimiento,
    String archivoUrl
) {}
