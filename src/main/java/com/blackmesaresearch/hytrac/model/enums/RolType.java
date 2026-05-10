package com.blackmesaresearch.hytrac.model.enums;

/**
 * Mapped to table: Rol
 * Useful for Spring Security @PreAuthorize("hasRole('ADMIN')")
 */
public enum RolType {
    ADMIN(1, "ADMIN"),
    OPERADOR(2, "OPERADOR"),
    TRANSPORTISTA(3, "TRANSPORTISTA");

    private final int id;
    private final String nombre;

    RolType(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    public static RolType fromId(int id) {
        for (RolType r : values()) {
            if (r.id == id) return r;
        }
        return null;
    }
}
