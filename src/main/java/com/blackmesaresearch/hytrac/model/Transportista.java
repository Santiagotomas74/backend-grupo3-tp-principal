package com.blackmesaresearch.hytrac.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;


@Entity
@Table(name = "Transportista")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Transportista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnetoOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "tipo_vinculo_id")
    private TipoVinculo tipoVinculo;

    @Column(unique = true, nullable = false)
    private String cuit;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private EmpresaTercerizada empresa;

    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;


}