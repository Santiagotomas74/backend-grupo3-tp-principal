package com.blackmesaresearch.hytrac.dto.csv;

import lombok.Data;

@Data
public class UsuarioCsv {
    private String nombre;
    private String apellido;
    private Long dni;
    private String email;
    private String legajo;
    private String password_raw;
    private String rol_nombre;
    private String lugar_nombre;
    private Integer activo;
}
