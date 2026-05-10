package com.blackmesaresearch.hytrac.model;

public class Permiso {
    private final Integer id;
    private final String codigo;
    private final String descripcion;

    public Permiso(Integer id, String codigo, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public Integer getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
}
