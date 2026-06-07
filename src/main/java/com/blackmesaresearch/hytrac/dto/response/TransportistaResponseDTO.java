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
        var usuario = t.getUsuario();
        var tipoVinculo = t.getTipoVinculo();

        String nombre = usuario != null ? usuario.getNombre() : null;
        String apellido = usuario != null ? usuario.getApellido() : null;
        String legajo = usuario != null ? usuario.getLegajo() : null;
        String tipoVinculoNombre = tipoVinculo != null ? tipoVinculo.getNombre() : null;

        return new TransportistaResponseDTO(
            t.getId(),
            nombre,
            apellido,
            t.getCuit(),
            legajo,
            t.getInicioActividad(),
            tipoVinculoNombre);
    }

}