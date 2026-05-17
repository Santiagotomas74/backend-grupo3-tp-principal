package com.blackmesaresearch.hytrac.dto.csv;

import lombok.Data;

@Data
public class LocalidadCsv {
    private String nombre;
    private String provincia_nombre; // Matches the column name in your CSV
    private String codigo_postal;
}