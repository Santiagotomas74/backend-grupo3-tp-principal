package com.blackmesaresearch.hytrac.dto.csv;

import lombok.Data;

@Data
public class EmpresaTercerizadaCsv {
    private String nombre_fantasia;
    private String razon_social;
    private String cuit;
    private String direccion;
    private String localidad_nombre;
    private String telefono;
    private Integer activo;
}
