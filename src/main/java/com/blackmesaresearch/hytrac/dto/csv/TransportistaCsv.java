package com.blackmesaresearch.hytrac.dto.csv;

import lombok.Data;

@Data
public class TransportistaCsv {
    private String usuario_email;
    private String usuario_legajo;
    private String tipo_vinculo_nombre;
    private String cuit;
    private String empresa_nombre;
    private Integer activo;
}
