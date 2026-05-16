package com.blackmesaresearch.hytrac.model.reference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Localidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Localidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "provincia_id")
    private Provincia provincia;

    @Column(name = "codigo_postal", nullable = false)
    private String codigoPostal;
}