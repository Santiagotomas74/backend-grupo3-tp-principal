package com.blackmesaresearch.hytrac.model.reference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "Combustible")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Combustible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(name = "numero_onu", nullable = false)
    private String numeroOnu;

    @Column(name = "clase_riesgo", nullable = false)
    private String claseRiesgo;

    @Column(name = "densidad", nullable = false)
    private Double densidad;

    @Column(name = "temperatura_referencia", nullable = false)
    private Double temperaturaReferencia;

}