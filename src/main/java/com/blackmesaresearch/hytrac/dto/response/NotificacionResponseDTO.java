package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDateTime;

public record NotificacionResponseDTO(
    Long id,
    String descripcion,
    String tipoNotificacion,
    Boolean visto,
    String enlace,
    LocalDateTime fechaCreacion
) {}