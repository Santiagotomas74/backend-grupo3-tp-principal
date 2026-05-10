package com.blackmesaresearch.hytrac.model;

public class UsuarioRol {
    private final Integer usuarioId;
    private final Integer rolId;

    public UsuarioRol(Integer usuarioId, Integer rolId) {
        this.usuarioId = usuarioId;
        this.rolId = rolId;
    }

    public Integer getUsuarioId() { return usuarioId; }
    public Integer getRolId() { return rolId; }
}
