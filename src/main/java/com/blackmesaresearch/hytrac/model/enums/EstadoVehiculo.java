package com.blackmesaresearch.hytrac.model.enums;

/**
 * Mapped to table: Estado_Vehiculo
 */
public enum EstadoVehiculo {
    ACTIVO(1),
    EN_REPARACION(2),
    FUERA_DE_SERVICIO(3),
    BAJA(4);

    private final int id;
    EstadoVehiculo(int id) { this.id = id; }
    public int getId() { return id; }

    public static EstadoVehiculo fromId(int id) {
        for (EstadoVehiculo e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }
}

