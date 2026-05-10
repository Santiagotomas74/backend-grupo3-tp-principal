package com.blackmesaresearch.hytrac.model.core;

import com.blackmesaresearch.hytrac.model.lookup.EstadoOrdenCarga;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Auditoria_Estado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id")
    private OrdenCarga orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_anterior_id")
    private EstadoOrdenCarga estadoAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_nuevo_id")
    private EstadoOrdenCarga estadoNuevo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmador_id")
    private Usuario confirmador;

    @Column(columnDefinition = "TEXT")
    private String motivo;
}