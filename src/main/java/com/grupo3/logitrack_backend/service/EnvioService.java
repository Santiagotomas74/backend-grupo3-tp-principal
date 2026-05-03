package com.grupo3.logitrack_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.grupo3.logitrack_backend.model.*;
import com.grupo3.logitrack_backend.repository.EnvioRepository;
import com.grupo3.logitrack_backend.repository.HistorialEstadoRepository;

@Service
public class EnvioService {

  private final EnvioRepository envioRepo;
  private final HistorialEstadoRepository historialEstadoRepo;
  private final PrioridadService prioridadService;

  public EnvioService(EnvioRepository envioRepo,
      HistorialEstadoRepository historialEstadoRepo,
      PrioridadService prioridadService) {
    this.envioRepo = envioRepo;
    this.historialEstadoRepo = historialEstadoRepo;
    this.prioridadService = prioridadService;

    System.out.println("EnvioService cargado correctamente");
  }

  public Envio crearEnvio(Envio envio) {

    validarDireccion(envio.getOrigen(), "origen");
    validarDireccion(envio.getDestino(), "destino");

    // Defaults (cleaner)
    int distancia = Optional.ofNullable(envio.getDistanciaEstimada()).orElse(500);
    TipoEnvio tipo = envio.getTipoEnvio();

    int volumen = Optional.ofNullable(envio.getVolumen()).orElse(5);

    boolean fragil = envio.isFragil();
    boolean frio = envio.isFrio();

    Saturacion saturacion = envio.getSaturacion();
    int ventanaHoras = Optional.ofNullable(envio.getVentanaHoras()).orElse(24);

    Prioridad prioridad = prioridadService.predecirPrioridad(
        distancia,
        tipo.getCode(),
        ventanaHoras,
        volumen,
        fragil,
        frio,
        saturacion.getCode());

    envio.setTrackingId(generarTrackingId());
    envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);
    envio.setFechaCreacion(LocalDateTime.now());
    envio.setPrioridadEnvio(prioridad);

    return envioRepo.save(envio);
  }

  private void validarDireccion(String direccion, String campo) {
    if (direccion == null || direccion.trim().length() < 6) {
      throw new IllegalArgumentException(
          "El campo '" + campo + "' debe tener al menos 6 caracteres");
    }
  }

  // ✅ Better tracking ID (no race condition)
  private String generarTrackingId() {
    return "LT-" + System.currentTimeMillis();
  }

  public List<Envio> listarTodosEnvios() {
    return envioRepo.findAll();
  }

  public Optional<Envio> buscarEnvioPorTrackingID(String trackingId) {
    return envioRepo.findByTrackingId(trackingId);
  }

  public List<Envio> buscarEnvioPorEstado(EstadoEnvio estadoEnvio) {
    return envioRepo.findByEstadoEnvio(estadoEnvio);
  }

  public Optional<Envio> actualizarEstadoEnvio(String trackingId,
      EstadoEnvio nuevoEstado,
      String motivoCambio,
      String cambiadoPor) {

    return envioRepo.findByTrackingId(trackingId).map(envio -> {

      EstadoEnvio estadoAnterior = envio.getEstadoEnvio();
      validarTransicionEstado(estadoAnterior, nuevoEstado);

      envio.setEstadoEnvio(nuevoEstado);
      envioRepo.save(envio);

      HistorialEstado historial = new HistorialEstado();
      historial.setEnvio(envio);
      historial.setEstadoAnterior(estadoAnterior);
      historial.setEstadoNuevo(nuevoEstado);
      historial.setMotivoCambio(motivoCambio);
      historial.setCambiadoPor(cambiadoPor);
      historial.setFechaCambio(LocalDateTime.now());

      historialEstadoRepo.save(historial);

      return envio;
    });
  }

  private void validarTransicionEstado(
      EstadoEnvio estadoActual,
      EstadoEnvio nuevoEstado) {
    if (estadoActual == null || nuevoEstado == null) {
      throw new IllegalArgumentException("Estado inválido");
    }

    if (estadoActual == nuevoEstado) {
      throw new IllegalStateException(
          "El envío ya se encuentra en estado "
              + nuevoEstado.toPrettyString());
    }

    if (!estadoActual.puedeCambiarA(nuevoEstado)) {
      throw new IllegalStateException(
          "No se puede pasar de "
              + estadoActual.toPrettyString()
              + " a "
              + nuevoEstado.toPrettyString());
    }
  }

  public List<HistorialEstado> verHistorialEstado(String trackingId) {

    Envio envio = envioRepo.findByTrackingId(trackingId)
        .orElseThrow(() -> new RuntimeException("Envío no encontrado"));

    return historialEstadoRepo
        .findByEnvioIdOrderByFechaCambioDesc(envio.getId());
  }

  // Calcular métricas para supervisores
  public MetricasDTO calcularMetricas() {
    List<Envio> todosEnvios = envioRepo.findAll();

    if (todosEnvios.isEmpty()) {
      return new MetricasDTO(0, 0, 0, 0, 0, 0, 0);
    }

    int total = todosEnvios.size();
    long pendientes = todosEnvios.stream().filter(e -> e.getEstadoEnvio() == EstadoEnvio.PENDIENTE).count();
    long enViaje = todosEnvios.stream().filter(e -> e.getEstadoEnvio() == EstadoEnvio.EN_VIAJE).count();
    long entregados = todosEnvios.stream().filter(e -> e.getEstadoEnvio() == EstadoEnvio.ENTREGADO).count();
    long cancelados = todosEnvios.stream().filter(e -> e.getEstadoEnvio() == EstadoEnvio.CANCELADO).count();

    double porcentajePendientes = (pendientes * 100.0) / total;
    double porcentajeEnTransito = (enViaje * 100.0) / total;
    double porcentajeEntregados = (entregados * 100.0) / total;
    double porcentajeCancelados = (cancelados * 100.0) / total;

    int distanciaTotal = todosEnvios.stream()
        .mapToInt(e -> Optional.ofNullable(e.getDistanciaEstimada()).orElse(0))
        .sum();

    double volumenTotal = todosEnvios.stream()
        .mapToDouble(e -> Optional.ofNullable(e.getVolumen()).orElse(0))
        .sum();

    return new MetricasDTO(
        total,
        porcentajePendientes,
        porcentajeEnTransito,
        porcentajeEntregados,
        porcentajeCancelados,
        distanciaTotal,
        volumenTotal);
  }
}