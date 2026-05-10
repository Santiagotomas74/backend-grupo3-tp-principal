package com.blackmesaresearch.hytrac.model.core;

import com.blackmesaresearch.hytrac.model.lookup.TipoIncidencia;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Incidencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id")
    private OrdenCarga orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_registro_id")
    private Usuario usuarioRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_gestion_id")
    private Usuario usuarioGestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_incidencia_id")
    private TipoIncidencia tipoIncidencia;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_incidente")
    private LocalDateTime fechaIncidente;

    @Column(name = "ley_aplicada")
    private String leyAplicada;

    @Column(name = "acciones_tomadas", columnDefinition = "TEXT")
    private String accionesTomadas;

    @Column(nullable = false)
    private Boolean resuelto = false;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
}