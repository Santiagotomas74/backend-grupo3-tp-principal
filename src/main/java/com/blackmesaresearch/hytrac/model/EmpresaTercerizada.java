package com.blackmesaresearch.hytrac.model;

import java.time.LocalDateTime;

public class EmpresaTercerizada {
    private final Integer id;
    private final String nombreFantasia;
    private final String razonSocial;
    private final String cuit;
    private final String direccion;
    private final Integer localidadId;
    private final String telefono;
    private final boolean activo;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public EmpresaTercerizada(Integer id, String nombreFantasia, String razonSocial, String cuit,
            String direccion, Integer localidadId, String telefono, boolean activo,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombreFantasia = nombreFantasia;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.direccion = direccion;
        this.localidadId = localidadId;
        this.telefono = telefono;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public String getDireccion() {
        return direccion;
    }

    public Integer getLocalidadId() {
        return localidadId;
    }

    public String getTelefono() {
        return telefono;
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