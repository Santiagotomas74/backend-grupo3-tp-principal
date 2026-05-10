package com.blackmesaresearch.hytrac.model;

import java.time.LocalDateTime;

public class Incidencia {
    private final Integer id;
    private final Integer ordenId;
    private final Integer usuarioRegistroId;
    private final Integer usuarioGestionId;
    private final Integer tipoIncidenciaId;
    private final String descripcion;
    private final LocalDateTime fechaIncidente;
    private final String leyAplicada;
    private final String accionesTomadas;
    private final boolean resuelto;
    private final LocalDateTime fechaResolucion;

    public Incidencia(Integer id, Integer ordenId, Integer usuarioRegistroId, Integer usuarioGestionId,
            Integer tipoIncidenciaId, String descripcion, LocalDateTime fechaIncidente,
            String leyAplicada, String accionesTomadas, boolean resuelto, LocalDateTime fechaResolucion) {
        this.id = id;
        this.ordenId = ordenId;
        this.usuarioRegistroId = usuarioRegistroId;
        this.usuarioGestionId = usuarioGestionId;
        this.tipoIncidenciaId = tipoIncidenciaId;
        this.descripcion = descripcion;
        this.fechaIncidente = fechaIncidente;
        this.leyAplicada = leyAplicada;
        this.accionesTomadas = accionesTomadas;
        this.resuelto = resuelto;
        this.fechaResolucion = fechaResolucion;
    }

    public Integer getId() {
        return id;
    }

    public Integer getOrdenId() {
        return ordenId;
    }

    public Integer getUsuarioRegistroId() {
        return usuarioRegistroId;
    }

    public Integer getUsuarioGestionId() {
        return usuarioGestionId;
    }

    public Integer getTipoIncidenciaId() {
        return tipoIncidenciaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaIncidente() {
        return fechaIncidente;
    }

    public String getLeyAplicada() {
        return leyAplicada;
    }

    public String getAccionesTomadas() {
        return accionesTomadas;
    }

    public boolean isResuelto() {
        return resuelto;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }
}
