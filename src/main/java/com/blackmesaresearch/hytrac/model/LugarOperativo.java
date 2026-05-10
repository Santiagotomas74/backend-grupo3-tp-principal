package com.blackmesaresearch.hytrac.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LugarOperativo {
    private final Integer id;
    private final Integer tipoId;
    private final String nombre;
    private final String direccion;
    private final Integer localidadId;
    private final BigDecimal latitud;
    private final BigDecimal longitud;
    private final boolean puedeRecibir;
    private final boolean puedeDespachar;
    private final boolean activo;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public LugarOperativo(Integer id, Integer tipoId, String nombre, String direccion, Integer localidadId,
            BigDecimal latitud, BigDecimal longitud, boolean puedeRecibir,
            boolean puedeDespachar, boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tipoId = tipoId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.localidadId = localidadId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.puedeRecibir = puedeRecibir;
        this.puedeDespachar = puedeDespachar;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTipoId() {
        return tipoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public Integer getLocalidadId() {
        return localidadId;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public boolean isPuedeRecibir() {
        return puedeRecibir;
    }

    public boolean isPuedeDespachar() {
        return puedeDespachar;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}