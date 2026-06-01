package com.blackmesaresearch.hytrac.dto.response;
import java.util.List;

import lombok.Data;

@Data
public class DeudasResponseDTO {
    private int status;
    private Results results;

    @Data
    public static class Results {
        private long identificacion;
        private String denominacion;
        private List<Periodo> periodos;
    }

    @Data
    public static class Periodo {
        private String fecha;
        private List<Entidad> entidades;
    }

    @Data
    public static class Entidad {
        private String entidad;
        private int situacion;
        private String fechaSit1;
        private double monto;
        private int diasAtrasoPago;
        private boolean refinanciaciones;
        private boolean recategorizacionOblig;
        private boolean situacionJuridica;
        private boolean irrecDisposicionTecnica;
        private boolean enRevision;
        private boolean procesoJud;
    }
    
}
