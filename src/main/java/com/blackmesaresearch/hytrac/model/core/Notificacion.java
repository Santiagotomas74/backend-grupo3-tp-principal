package com.blackmesaresearch.hytrac.model.core;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificacion")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legajo_receptor", nullable = false)
    private String legajoReceptor;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "tipo_notificacion", nullable = false)
    private String tipoNotificacion;

    @Column(name = "visto")
    private Boolean visto = false;

    @Column(name = "enlace")
    private String enlace;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() { this.fechaCreacion = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLegajoReceptor() { return legajoReceptor; }
    public void setLegajoReceptor(String legajoReceptor) { this.legajoReceptor = legajoReceptor; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipoNotificacion() { return tipoNotificacion; }
    public void setTipoNotificacion(String tipoNotificacion) { this.tipoNotificacion = tipoNotificacion; }
    public Boolean getVisto() { return visto; }
    public void setVisto(Boolean visto) { this.visto = visto; }
    public String getEnlace() { return enlace; }
    public void setEnlace(String enlace) { this.enlace = enlace; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}