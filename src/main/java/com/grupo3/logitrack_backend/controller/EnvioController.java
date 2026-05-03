package com.grupo3.logitrack_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo3.logitrack_backend.model.Envio;
import com.grupo3.logitrack_backend.model.EstadoEnvio;
import com.grupo3.logitrack_backend.model.HistorialEstado;
import com.grupo3.logitrack_backend.model.MetricasDTO;
import com.grupo3.logitrack_backend.service.EnvioService;

@RestController
@RequestMapping("/api/envios")
@CrossOrigin(origins = "http://localhost:5173") // Para el react

public class EnvioController {

  private final EnvioService envioService;

  public EnvioController(EnvioService envioService) {
    this.envioService = envioService;
  }

  @GetMapping
  public List<Envio> listarTodosLosEnvios() {
    return envioService.listarTodosEnvios();
  }

  @GetMapping("/{trackingId}")
  public ResponseEntity<Envio> obtenerDetalleEnvio(@PathVariable String trackingId) {
    return envioService.buscarEnvioPorTrackingID(trackingId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<?> crearEnvio(@RequestBody Envio envio) {
    try {
      Envio nuevoEnvio = envioService.crearEnvio(envio);
      return ResponseEntity.ok(nuevoEnvio);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
  }

  @PatchMapping("/{trackingId}/estado")
  public ResponseEntity<?> cambiarEstadoEnvio(@PathVariable String trackingId, @RequestBody Map<String, String> body) {
    try {
      EstadoEnvio nuevoEstado = EstadoEnvio.valueOf(body.get("estado"));
      String motivoCambio = body.get("motivo");
      String cambiadoPor = body.get("usuario");

      return envioService.actualizarEstadoEnvio(trackingId, nuevoEstado, motivoCambio, cambiadoPor)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (IllegalArgumentException | IllegalStateException ex) {
      return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

  }

  @GetMapping("/{trackingId}/historial")
  public ResponseEntity<List<HistorialEstado>> obtenerHistorialEstado(@PathVariable String trackingId) {
    List<HistorialEstado> historial = envioService.verHistorialEstado(trackingId);
    return ResponseEntity.ok(historial);
  }

  @GetMapping("/estado/{estado}")
  public List<Envio> porEstado(@PathVariable EstadoEnvio estado) {
    return envioService.buscarEnvioPorEstado(estado);
  }

  @GetMapping("/metricas")
  public ResponseEntity<MetricasDTO> obtenerMetricas() {
    MetricasDTO metricas = envioService.calcularMetricas();
    return ResponseEntity.ok(metricas);
  }

}