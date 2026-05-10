package com.blackmesaresearch.hytrac.model;

import java.time.LocalDateTime;

public class AuditoriaEstado {
    private final Integer id;
    private final Integer ordenId;
    private final Integer estadoAnteriorId;
    private final Integer estadoNuevoId;
    private final LocalDateTime fechaCambio;
    private final Integer solicitanteId;
    private final Integer confirmadorId;
    private final String motivo;

    public AuditoriaEstado(Integer id, Integer ordenId, Integer estadoAnteriorId, Integer estadoNuevoId, 
                           LocalDateTime fechaCambio, Integer solicitanteId, Integer confirmadorId, String motivo) {
        this.id = id;
        this.ordenId = ordenId;
        this.estadoAnteriorId = estadoAnteriorId;
        this.estadoNuevoId = estadoNuevoId;
        this.fechaCambio = fechaCambio;
        this.solicitanteId = solicitanteId;
        this.confirmadorId = confirmadorId;
        this.motivo = motivo;
    }

    public Integer getId() { return id; }
    public Integer getOrdenId() { return ordenId; }
    public Integer getEstadoAnteriorId() { return estadoAnteriorId; }
    public Integer getEstadoNuevoId() { return estadoNuevoId; }
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public Integer getSolicitanteId() { return solicitanteId; }
    public Integer getConfirmadorId() { return confirmadorId; }
    public String getMotivo() { return motivo; }
}