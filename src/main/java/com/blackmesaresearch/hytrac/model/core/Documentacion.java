package com.blackmesaresearch.hytrac.model.core;

import com.blackmesaresearch.hytrac.model.lookup.TipoDocumento;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Documentacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipo_documento_id")
    private TipoDocumento tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camion_id")
    private Vehiculo camion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acoplado_id")
    private Acoplado acoplado;

    @Column(name = "nro_documento")
    private String nroDocumento;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "archivo_url", length = 500)
    private String archivoUrl;

    @Column(name = "estado_verificacion")
    private Boolean estadoVerificacion = false;

    @Column(name = "comentarios", columnDefinition = "TEXT")
    private String comentarios;
}