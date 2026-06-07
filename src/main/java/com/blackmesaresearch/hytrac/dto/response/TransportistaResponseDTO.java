package com.blackmesaresearch.hytrac.dto.response;

import java.time.LocalDate;

import com.blackmesaresearch.hytrac.model.core.Transportista;

public record TransportistaResponseDTO(

        Integer id,

        String nombre,
        String apellido,

        String cuit,
        String legajo,

        LocalDate inicioActividad,

        String tipoVinculo

) {

    public static TransportistaResponseDTO from(Transportista t) {
        return new TransportistaResponseDTO(
                t.getId(),
                t.getUsuario().getNombre(),
                t.getUsuario().getApellido(),
                t.getCuit(),
                t.getUsuario().getLegajo(),
                t.getInicioActividad(),
                t.getTipoVinculo().getNombre());
    }

}