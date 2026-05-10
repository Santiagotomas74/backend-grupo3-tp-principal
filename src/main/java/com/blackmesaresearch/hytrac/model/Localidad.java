package com.blackmesaresearch.hytrac.model;

public class Localidad {
    private final Integer id;
    private final Integer provinciaId;
    private final String nombre;
    private final String codigoPostal;

    public Localidad(Integer id, Integer provinciaId, String nombre, String codigoPostal) {
        this.id = id;
        this.provinciaId = provinciaId;
        this.nombre = nombre;
        this.codigoPostal = codigoPostal;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProvinciaId() {
        return provinciaId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }
}
