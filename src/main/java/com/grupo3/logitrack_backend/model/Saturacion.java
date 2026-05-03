package com.grupo3.logitrack_backend.model;

public enum Saturacion {
  BAJA(0),
  MEDIA(1),
  ALTA(2);

  private final int code;

  Saturacion(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static Saturacion fromCode(int code) {
    for (Saturacion s : values()) {
      if (s.code == code) {
        return s;
      }
    }
    throw new IllegalArgumentException("Invalid code: " + code);
  }
}