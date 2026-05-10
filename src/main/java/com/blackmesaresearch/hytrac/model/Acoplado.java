package com.blackmesaresearch.hytrac.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Acoplado {
    private final Integer id;
    private final String patente;
    private final BigDecimal capacidadMaximaLitros;
    private final Integer empresaId;
    private final Integer estadoId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Acoplado(Integer id, String patente, BigDecimal capacidadMaximaLitros,
            Integer empresaId, Integer estadoId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patente = patente;
        this.capacidadMaximaLitros = capacidadMaximaLitros;
        this.empresaId = empresaId;
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
        return capacidadMaximaLitros;
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
