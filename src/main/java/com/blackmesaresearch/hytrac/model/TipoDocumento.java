package com.blackmesaresearch.hytrac.model;

public class TipoDocumento {
    private final Integer id;
    private final String nombre;
    private final String categoria;

    public TipoDocumento(Integer id, String nombre, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
}