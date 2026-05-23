package com.blackmesaresearch.hytrac.model.lookup;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Estado_Vehiculo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;
}