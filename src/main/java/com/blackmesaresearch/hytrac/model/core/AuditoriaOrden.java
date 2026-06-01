package com.blackmesaresearch.hytrac.model.core;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "auditoria_orden")
public class AuditoriaOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // =========================
    // DATOS ORDEN
    // =========================

    @Column(name = "orden_numero_remito", nullable = false)
    private String ordenNumeroRemito;

    // =========================
    // ESTADOS
    // =========================

    @Column(name = "estado_anterior_nombre")
    private String estadoAnteriorNombre;

    @Column(name = "estado_nuevo_nombre")
    private String estadoNuevoNombre;

    // =========================
    // FECHA
    // =========================

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    // =========================
    // USUARIOS
    // =========================

    @Column(name = "solicitante_legajo")
    private String solicitanteLegajo;

    @Column(name = "confirmador_legajo")
    private String confirmadorLegajo;

    // =========================
    // MOTIVO
    // =========================

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrdenNumeroRemito() {
        return ordenNumeroRemito;
    }

    public void setOrdenNumeroRemito(String ordenNumeroRemito) {
        this.ordenNumeroRemito = ordenNumeroRemito;
    }

    public String getEstadoAnteriorNombre() {
        return estadoAnteriorNombre;
    }

    public void setEstadoAnteriorNombre(String estadoAnteriorNombre) {
        this.estadoAnteriorNombre = estadoAnteriorNombre;
    }

    public String getEstadoNuevoNombre() {
        return estadoNuevoNombre;
    }

    public void setEstadoNuevoNombre(String estadoNuevoNombre) {
        this.estadoNuevoNombre = estadoNuevoNombre;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getSolicitanteLegajo() {
        return solicitanteLegajo;
    }

    public void setSolicitanteLegajo(String solicitanteLegajo) {
        this.solicitanteLegajo = solicitanteLegajo;
    }

    public String getConfirmadorLegajo() {
        return confirmadorLegajo;
    }

    public void setConfirmadorLegajo(String confirmadorLegajo) {
        this.confirmadorLegajo = confirmadorLegajo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}