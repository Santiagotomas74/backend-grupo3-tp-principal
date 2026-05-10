package com.blackmesaresearch.hytrac.model.core;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.blackmesaresearch.hytrac.model.lookup.EstadoVehiculo;

import java.time.LocalDateTime;


@Entity
@Table(name = "Acoplado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor

public class Acoplado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String patente;

    @Column(name = "capacidad_maxima_litros", nullable = false)
    private Double capacidadMaximaLitros;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private EmpresaTercerizada empresa;

    @ManyToOne
    @JoinColumn(name = "estado_vehiculo_id")
    private EstadoVehiculo estado;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    

}