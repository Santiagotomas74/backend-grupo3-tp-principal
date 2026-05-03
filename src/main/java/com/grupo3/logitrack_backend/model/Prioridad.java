package com.grupo3.logitrack_backend.model;

public enum Prioridad {
  BAJA(0),
  MEDIA(1),
  ALTA(2);

  private final int code;

  Prioridad(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static Prioridad fromCode(int code) {
    for (Prioridad p : values()) {
      if (p.code == code) {
        return p;
      }
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }
}