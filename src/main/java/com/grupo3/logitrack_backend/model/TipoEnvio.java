package com.grupo3.logitrack_backend.model;

public enum TipoEnvio {
  NORMAL(0),
  EXPRESS(1);

  private final int code;

  TipoEnvio(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static TipoEnvio fromCode(int code) {
    switch (code) {
      case 0:
        return NORMAL;
      case 1:
        return EXPRESS;
      default:
        throw new IllegalArgumentException("Invalid code: " + code);
    }
  }
}