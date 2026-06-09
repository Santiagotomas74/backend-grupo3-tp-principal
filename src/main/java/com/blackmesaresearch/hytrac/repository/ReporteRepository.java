package com.blackmesaresearch.hytrac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.blackmesaresearch.hytrac.dto.response.EstadoMetricaDTO;
import com.blackmesaresearch.hytrac.model.core.OrdenCarga;

@Repository
public interface ReporteRepository extends JpaRepository<OrdenCarga, Integer> {
// Consultas para métricas del dashboard de envíos y desglose por estado de los envíos
@Query("SELECT COUNT(o) FROM OrdenCarga o")
Integer countTotalEnvios();

@Query("SELECT COUNT(o) FROM OrdenCarga o WHERE o.estadoOrdenCarga.nombre = 'EN_VIAJE'")
Integer countEnviosEnViaje();

@Query("SELECT COUNT(o) FROM OrdenCarga o WHERE o.estadoOrdenCarga.nombre = 'ENTREGADO'")
Integer countEnviosEntregados();

@Query("SELECT new com.blackmesaresearch.hytrac.dto.response.EstadoMetricaDTO(o.estadoOrdenCarga.nombre, CAST(COUNT(o) AS int)) " +
    "FROM OrdenCarga o GROUP BY o.estadoOrdenCarga.nombre")
    
List<EstadoMetricaDTO> obtenerDesglosePorEstado();
}