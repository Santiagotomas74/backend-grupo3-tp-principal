package com.blackmesaresearch.hytrac.model;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "Empresa_Tercerizada")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmpresaTercerizada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_fantasia", nullable = false)
    private String nombreFantasia;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "cuit", unique = true, nullable = false)
    private String cuit;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @CreationTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

}