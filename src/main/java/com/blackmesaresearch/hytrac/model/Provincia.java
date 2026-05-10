package com.blackmesaresearch.hytrac.model;

public class Provincia {
    private final Integer id;
    private final String nombre;

    public Provincia(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
