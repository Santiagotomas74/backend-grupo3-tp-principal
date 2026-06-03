package com.blackmesaresearch.hytrac.dto.request;
import java.util.List;

public record AltaTransportistaRequestDTO (
    String nombre,
    String apellido,
    Long dni,
    String email,
    String passwordTemporal,

    String cuit,
    Integer empresaId,
    Integer tipoVinculoId,

    List<DocumentoRequestDTO> documentos
) {}


    

