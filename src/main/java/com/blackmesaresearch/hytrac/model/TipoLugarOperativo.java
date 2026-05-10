// TipoLugarOperativo.java
package com.blackmesaresearch.hytrac.model;

public class TipoLugarOperativo {
    private final Integer id;
    private final String nombre;

    public TipoLugarOperativo(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
}