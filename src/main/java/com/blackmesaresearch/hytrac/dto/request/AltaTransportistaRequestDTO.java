package com.blackmesaresearch.hytrac.dto.request;
import java.time.LocalDate;
import java.util.List;

public record AltaTransportistaRequestDTO (
    String nombre,
    String apellido,
    Long dni,
    String email,
    String passwordTemporal,

    LocalDate inicioActividad,

    String cuit,
    Integer empresaId,
    Integer tipoVinculoId,

    List<DocumentoRequestDTO> documentos
) {}


    

