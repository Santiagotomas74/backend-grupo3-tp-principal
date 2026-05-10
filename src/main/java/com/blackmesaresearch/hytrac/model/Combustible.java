package com.blackmesaresearch.hytrac.model;

import java.math.BigDecimal;

public class Combustible {
    private final Integer id;
    private final String nombre;
    private final String numeroOnu;
    private final String claseRiesgo;
    private final BigDecimal densidad;
    private final BigDecimal temperaturaReferencia;

    public Combustible(Integer id, String nombre, String numeroOnu, String claseRiesgo,
            BigDecimal densidad, BigDecimal temperaturaReferencia) {
        this.id = id;
        this.nombre = nombre;
        this.numeroOnu = numeroOnu;
        this.claseRiesgo = claseRiesgo;
        this.densidad = densidad;
        this.temperaturaReferencia = temperaturaReferencia;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNumeroOnu() {
        return numeroOnu;
    }

    public String getClaseRiesgo() {
        return claseRiesgo;
    }

    public BigDecimal getDensidad() {
        return densidad;
    }

    public BigDecimal getTemperaturaReferencia() {
        return temperaturaReferencia;
    }
}
