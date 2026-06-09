package com.blackmesaresearch.hytrac.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blackmesaresearch.hytrac.dto.response.NotificacionResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Notificacion;
import com.blackmesaresearch.hytrac.repository.NotificacionRepository;

@Service
public class NotificacionService {
    private final NotificacionRepository repo;

    public NotificacionService(NotificacionRepository repo) { this.repo = repo; }
    

    public void crearNotificacion(String legajo, String desc, String tipo) {
        Notificacion n = new Notificacion();
        n.setLegajoReceptor(legajo);
        n.setDescripcion(desc);
        n.setVisto(false);
        repo.save(n);
    }

    public List<NotificacionResponseDTO> obtenerPorLegajo(String legajo) {
        return repo.findByLegajoReceptorOrderByFechaCreacionDesc(legajo)
            .stream()
            .map(n -> new NotificacionResponseDTO(
                n.getId(), n.getDescripcion(), 
                n.getVisto(), n.getFechaCreacion()
            ))
            .toList();
    }
    @Transactional
    public void marcarComoVisto(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setVisto(true);
            repo.save(n);
        });
    }
}