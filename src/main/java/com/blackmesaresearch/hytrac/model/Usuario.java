package com.blackmesaresearch.hytrac.model;

import java.time.LocalDateTime;

public class Usuario {
    private final Integer id;
    private final String nombre;
    private final String apellido;
    private final Long dni;
    private final String email;
    private final String legajo;
    private final String passwordHash;
    private final boolean activo;
    private final Integer lugarOperativoId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Usuario(Integer id, String nombre, String apellido, Long dni, String email,
            String legajo, String passwordHash, boolean activo,
            Integer lugarOperativoId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.legajo = legajo;
        this.passwordHash = passwordHash;
        this.activo = activo;
        this.lugarOperativoId = lugarOperativoId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Long getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getLegajo() {
        return legajo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActivo() {
        return activo;
    }

    public Integer getLugarOperativoId() {
        return lugarOperativoId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}