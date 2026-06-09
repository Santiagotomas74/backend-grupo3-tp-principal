package com.blackmesaresearch.hytrac.model.core;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.blackmesaresearch.hytrac.model.lookup.TipoLugarOperativo;
import com.blackmesaresearch.hytrac.model.reference.Localidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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