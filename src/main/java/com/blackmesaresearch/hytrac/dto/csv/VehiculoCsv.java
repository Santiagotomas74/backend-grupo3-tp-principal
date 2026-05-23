package com.blackmesaresearch.hytrac.dto.csv;

import lombok.Data;

@Data
public class VehiculoCsv {
    private String patente;
    private String empresa_nombre;
    private Double peso_maximo_admitido;
    private String marca;
    private String modelo;
    private String estado_nombre;
}
