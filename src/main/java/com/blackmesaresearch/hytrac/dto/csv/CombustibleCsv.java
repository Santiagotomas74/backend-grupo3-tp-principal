package com.blackmesaresearch.hytrac.dto.csv;

import lombok.Data;

@Data
public class CombustibleCsv {
    private String nombre;
    private String numero_onu;
    private String clase_riesgo;
    private String densidad;
    private String temperatura_referencia;
}
