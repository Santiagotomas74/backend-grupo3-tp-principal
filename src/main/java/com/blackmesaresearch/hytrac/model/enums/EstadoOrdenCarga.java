package com.blackmesaresearch.hytrac.model.enums;

/**
 * Mapped to table: Estado_Orden_Carga
 */
public enum EstadoOrdenCarga {
    BORRADOR(1),
    PROGRAMADA(2),
    EN_TRANSITO(3),
    ENTREGADA(4),
    CANCELADA(5);

    private final int id;
    EstadoOrdenCarga(int id) { this.id = id; }
    public int getId() { return id; }

    public static EstadoOrdenCarga fromId(int id) {
        for (EstadoOrdenCarga e : values()) {
            if (e.id == id) return e;
        }
        return null; 
    }
}