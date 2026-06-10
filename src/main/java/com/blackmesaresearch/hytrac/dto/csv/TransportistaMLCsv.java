package com.blackmesaresearch.hytrac.dto.csv;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TransportistaMLCsv {

    // datos
    private String nombre;
    private String apellido;
    private String usuario_email;
    private String usuario_legajo;
    private String tipo_vinculo_nombre;
    private Long dni;
    private String cuit;
    private String empresa_nombre;
    private Integer disponible;
    private LocalDate inicio_actividad;

    // stats
    private Integer total_ordenes;
    private Integer largas;
    private Integer largas_exitosas;
    private Integer medias;
    private Integer medias_exitosas;
    private Integer cortas;
    private Integer cortas_exitosas;
    private Integer pesadas;
    private Integer pesadas_exitosas;
    private Integer livianas;
    private Integer livianas_exitosas;
    private Integer incidencias_graves;

}
