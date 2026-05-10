package com.blackmesaresearch.hytrac.model;

import jakarta.persistence.*; 
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;


@Entity
@Table(name = "Vehiculo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true,nullable = false)
    private String patente;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private EmpresaTercerizada empresa;

    @Column(name = "capacidad_total_litros", nullable = false)
    private Double capacidadTotalLitros;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;


    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoVehiculo estado;

    @creationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
}
