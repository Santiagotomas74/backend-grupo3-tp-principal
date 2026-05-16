package com.blackmesaresearch.hytrac.model.core;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.blackmesaresearch.hytrac.model.lookup.TipoLugarOperativo;
import com.blackmesaresearch.hytrac.model.reference.Localidad;

import java.time.LocalDateTime;

@Entity
@Table(name = "LugarOperativo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LugarOperativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoLugarOperativo tipo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    private Double latitud;
    private Double longitud;

    @Column(name = "puede_recibir")
    private Boolean puedeRecibir;

    @Column(name = "puede_despachar")
    private Boolean puedeDespachar;

    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime fechaModificacion;

}