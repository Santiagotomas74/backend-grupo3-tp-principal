package com.blackmesaresearch.hytrac.model;

public class RolPermiso {
    private final Integer rolId;
    private final Integer permisoId;

    public RolPermiso(Integer rolId, Integer permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    public Integer getRolId() { return rolId; }
    public Integer getPermisoId() { return permisoId; }
}