package com.blackmesaresearch.hytrac.dto.csv;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrdenCargaCsv {
    private String numeroRemito;
    private String cot;
    private String camionPatente;
    private String acopladoPatente;
    private String transportistaLegajo;
    private String plantaDespachoNombre;
    private String estacionDestinoNombre;
    private String operadorLegajo;
    private String estadoNombre;
    private String combustibleNombre;
    private Double litrosCargados;
    private Double litrosEntregados;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaSalidaPlanta;
    private LocalDateTime fechaEntregaEstimada;
    private Double temperaturaCarga;
    private Double densidadCarga;
    private Boolean fieAdjunta;
    private String observaciones;
    private Boolean confirmado;
}
