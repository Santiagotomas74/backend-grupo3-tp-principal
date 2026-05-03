package com.grupo3.logitrack_backend.model;

public class MetricasDTO {
  private int totalEnvios;
  private double porcentajePendientes;
  private double porcentajeEnTransito;
  private double porcentajeEntregados;
  private double porcentajeCancelados;
  private int distanciaTotal;
  private double volumenTotal;

  public MetricasDTO(int totalEnvios, double porcentajePendientes, double porcentajeEnTransito,
      double porcentajeEntregados, double porcentajeCancelados, int distanciaTotal, double volumenTotal) {
    this.totalEnvios = totalEnvios;
    this.porcentajePendientes = porcentajePendientes;
    this.porcentajeEnTransito = porcentajeEnTransito;
    this.porcentajeEntregados = porcentajeEntregados;
    this.porcentajeCancelados = porcentajeCancelados;
    this.distanciaTotal = distanciaTotal;
    this.volumenTotal = volumenTotal;
  }

  public int getTotalEnvios() {
    return totalEnvios;
  }

  public double getPorcentajePendientes() {
    return porcentajePendientes;
  }

  public double getPorcentajeEnTransito() {
    return porcentajeEnTransito;
  }

  public double getPorcentajeEntregados() {
    return porcentajeEntregados;
  }

  public double getPorcentajeCancelados() {
    return porcentajeCancelados;
  }

  public int getDistanciaTotal() {
    return distanciaTotal;
  }

  public double getVolumenTotal() {
    return volumenTotal;
  }
}
