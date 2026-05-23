package com.blackmesaresearch.hytrac.dto.csv;

import lombok.Data;

@Data
public class LugarOperativoCsv {
    private String nombre;
    private String tipo_nombre;
    private String direccion;
    private String localidad_nombre;
    private Double latitud;
    private Double longitud;
    private Integer puede_recibir;
    private Integer puede_despachar;
    private Integer activo;
}