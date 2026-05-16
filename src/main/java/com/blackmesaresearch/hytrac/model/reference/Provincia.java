package com.blackmesaresearch.hytrac.model.reference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Provincia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Provincia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;
}