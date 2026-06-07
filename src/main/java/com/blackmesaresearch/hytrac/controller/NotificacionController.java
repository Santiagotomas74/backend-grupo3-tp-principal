package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackmesaresearch.hytrac.dto.response.NotificacionResponseDTO;
import com.blackmesaresearch.hytrac.service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    private final NotificacionService service;

    public NotificacionController(NotificacionService service) { this.service = service; }

    @GetMapping("/{legajo}")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerMisNotificaciones(@PathVariable String legajo) {
        return ResponseEntity.ok(service.obtenerPorLegajo(legajo));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        service.marcarComoVisto(id);
        return ResponseEntity.ok().build();
    }
}