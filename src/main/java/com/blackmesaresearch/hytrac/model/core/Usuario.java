package com.blackmesaresearch.hytrac.model.core;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.blackmesaresearch.hytrac.model.lookup.Rol;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table (name = "Usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "dni", unique = true, nullable = false)
    private Long dni;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "legajo", unique = true, nullable = false)
    private String legajo;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "lugar_operativo_id")
    private LugarOperativo lugarOperativo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "Usuario_Rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
}