package com.blackmesaresearch.hytrac.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.blackmesaresearch.hytrac.model.enums.EstadoOrdenCarga;

public class OrdenCarga {
    private final Integer id;
    private final String numeroRemito;
    private final String cot;
    private final Integer camionId;
    private final Integer acopladoId;
    private final Integer transportistaId;
    private final Integer plantaDespachoId;
    private final Integer estacionDestinoId;
    private final Integer operadorId;
    private final Integer combustibleId;
    private final EstadoOrdenCarga estado;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaSalidaPlanta;
    private final LocalDateTime fechaEntregaEstimada;
    private final LocalDateTime fechaEntregaReal;
    private final BigDecimal temperaturaCarga;
    private final BigDecimal densidadCarga;
    private final BigDecimal litrosCargados;
    private final BigDecimal litrosEntregados;
    private final boolean fieAdjunta;
    private final String observaciones;

    // EXACT CONSTRUCTOR MATCH
    public OrdenCarga(Integer id, String numeroRemito, String cot, Integer camionId, 
                      Integer acopladoId, Integer transportistaId, Integer plantaDespachoId, 
                      Integer estacionDestinoId, Integer operadorId, Integer combustibleId, 
                      EstadoOrdenCarga estado, LocalDateTime fechaCreacion, LocalDateTime fechaSalidaPlanta, 
                      LocalDateTime fechaEntregaEstimada, LocalDateTime fechaEntregaReal, 
                      BigDecimal temperaturaCarga, BigDecimal densidadCarga, 
                      BigDecimal litrosCargados, BigDecimal litrosEntregados, 
                      boolean fieAdjunta, String observaciones) {
        this.id = id;
        this.numeroRemito = numeroRemito;
        this.cot = cot;
        this.camionId = camionId;
        this.acopladoId = acopladoId;
        this.transportistaId = transportistaId;
        this.plantaDespachoId = plantaDespachoId;
        this.estacionDestinoId = estacionDestinoId;
        this.operadorId = operadorId;
        this.combustibleId = combustibleId;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaSalidaPlanta = fechaSalidaPlanta;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.fechaEntregaReal = fechaEntregaReal;
        this.temperaturaCarga = temperaturaCarga;
        this.densidadCarga = densidadCarga;
        this.litrosCargados = litrosCargados;
        this.litrosEntregados = litrosEntregados;
        this.fieAdjunta = fieAdjunta;
        this.observaciones = observaciones;
    }

    public Integer getId() {
        return id;
    }

    public String getNumeroRemito() {
        return numeroRemito;
    }

    public String getCot() {
        return cot;
    }

    public Integer getCamionId() {
        return camionId;
    }

    public Integer getAcopladoId() {
        return acopladoId;
    }

    public Integer getTransportistaId() {
        return transportistaId;
    }

    public Integer getPlantaDespachoId() {
        return plantaDespachoId;
    }

    public Integer getEstacionDestinoId() {
        return estacionDestinoId;
    }

    public Integer getOperadorId() {
        return operadorId;
    }

    public Integer getCombustibleId() {
        return combustibleId;
    }

    public EstadoOrdenCarga getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaSalidaPlanta() {
        return fechaSalidaPlanta;
    }

    public LocalDateTime getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public LocalDateTime getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public BigDecimal getTemperaturaCarga() {
        return temperaturaCarga;
    }

    public BigDecimal getDensidadCarga() {
        return densidadCarga;
    }

    public BigDecimal getLitrosCargados() {
        return litrosCargados;
    }

    public BigDecimal getLitrosEntregados() {
        return litrosEntregados;
    }

    public boolean isFieAdjunta() {
        return fieAdjunta;
    }

    public String getObservaciones() {
        return observaciones;
    }
}