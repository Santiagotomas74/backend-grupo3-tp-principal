package com.blackmesaresearch.hytrac.repository;

import com.blackmesaresearch.hytrac.model.OrdenCarga;
import com.blackmesaresearch.hytrac.model.enums.EstadoOrdenCarga;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrdenCargaRepository {

    private final JdbcClient jdbcClient;

    public OrdenCargaRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private final RowMapper<OrdenCarga> rowMapper = (rs, rowNum) -> new OrdenCarga(
        rs.getInt("id"),
        rs.getString("numero_remito"),
        rs.getString("cot"),
        rs.getObject("camion_id", Integer.class),
        rs.getObject("acoplado_id", Integer.class),
        rs.getObject("transportista_id", Integer.class),
        rs.getObject("planta_despacho_id", Integer.class),
        rs.getObject("estacion_destino_id", Integer.class),
        rs.getObject("operador_id", Integer.class),
        rs.getObject("combustible_id", Integer.class),
        EstadoOrdenCarga.fromId(rs.getInt("estado_id")),
        rs.getTimestamp("fecha_creacion") != null ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null,
        rs.getTimestamp("fecha_salida_planta") != null ? rs.getTimestamp("fecha_salida_planta").toLocalDateTime() : null,
        rs.getTimestamp("fecha_entrega_estimada") != null ? rs.getTimestamp("fecha_entrega_estimada").toLocalDateTime() : null,
        rs.getTimestamp("fecha_entrega_real") != null ? rs.getTimestamp("fecha_entrega_real").toLocalDateTime() : null,
        rs.getBigDecimal("temperatura_carga"),
        rs.getBigDecimal("densidad_carga"),
        rs.getBigDecimal("litros_cargados"),
        rs.getBigDecimal("litros_entregados"),
        rs.getBoolean("fie_adjunta"),
        rs.getString("observaciones")
    );

    public List<OrdenCarga> findAll() {
        return jdbcClient.sql("SELECT * FROM Orden_Carga")
                .query(rowMapper)
                .list();
    }

    public Optional<OrdenCarga> findById(Integer id) {
        return jdbcClient.sql("SELECT * FROM Orden_Carga WHERE id = :id")
                .param("id", id)
                .query(rowMapper)
                .optional();
    }

    public void updateEstado(Integer id, Integer nuevoEstadoId) {
        jdbcClient.sql("UPDATE Orden_Carga SET estado_id = :estadoId WHERE id = :id")
                .param("estadoId", nuevoEstadoId)
                .param("id", id)
                .update();
    }
}