package com.blackmesaresearch.hytrac.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Vehiculo {
    private final Integer id;
    private final String patente;
    private final Integer empresaId;
    private final BigDecimal capacidadTotalLitros;
    private final String marca;
    private final String modelo;
    private final Integer estadoId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Vehiculo(Integer id, String patente, Integer empresaId, BigDecimal capacidadTotalLitros,
            String marca, String modelo, Integer estadoId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patente = patente;
        this.empresaId = empresaId;
        this.capacidadTotalLitros = capacidadTotalLitros;
        this.marca = marca;
        this.modelo = modelo;
        this.estadoId = estadoId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public Integer getEmpresaId() {
        return empresaId;
    }

    public BigDecimal getCapacidadTotalLitros() {
        return capacidadTotalLitros;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
