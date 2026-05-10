package com.blackmesaresearch.hytrac.model;

import java.time.LocalDateTime;

public class Transportista {
    private final Integer id;
    private final Integer usuarioId;
    private final Integer tipoVinculoId;
    private final String cuit;
    private final Integer empresaId;
    private final boolean activo;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Transportista(Integer id, Integer usuarioId, Integer tipoVinculoId, String cuit, 
                         Integer empresaId, boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tipoVinculoId = tipoVinculoId;
        this.cuit = cuit;
        this.empresaId = empresaId;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public Integer getTipoVinculoId() { return tipoVinculoId; }
    public String getCuit() { return cuit; }
    public Integer getEmpresaId() { return empresaId; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}