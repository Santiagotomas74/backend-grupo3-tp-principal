package com.blackmesaresearch.hytrac.model;

import java.time.LocalDate;

public class Documentacion {
    private final Integer id;
    private final Integer tipoDocumentoId;
    private final Integer transportistaId;
    private final Integer camionId;
    private final Integer acopladoId;
    private final String nroDocumento;
    private final LocalDate fechaEmision;
    private final LocalDate fechaVencimiento;
    private final String archivoUrl;
    private final boolean estadoVerificacion;
    private final String comentarios;

    public Documentacion(Integer id, Integer tipoDocumentoId, Integer transportistaId, Integer camionId,
            Integer acopladoId, String nroDocumento, LocalDate fechaEmision,
            LocalDate fechaVencimiento, String archivoUrl, boolean estadoVerificacion, String comentarios) {
        this.id = id;
        this.tipoDocumentoId = tipoDocumentoId;
        this.transportistaId = transportistaId;
        this.camionId = camionId;
        this.acopladoId = acopladoId;
        this.nroDocumento = nroDocumento;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.archivoUrl = archivoUrl;
        this.estadoVerificacion = estadoVerificacion;
        this.comentarios = comentarios;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    public Integer getTransportistaId() {
        return transportistaId;
    }

    public Integer getCamionId() {
        return camionId;
    }

    public Integer getAcopladoId() {
        return acopladoId;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public boolean isEstadoVerificacion() {
        return estadoVerificacion;
    }

    public String getComentarios() {
        return comentarios;
    }
}
