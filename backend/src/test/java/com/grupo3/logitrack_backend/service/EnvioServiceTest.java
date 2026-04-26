package com.grupo3.logitrack_backend.service;

import com.grupo3.logitrack_backend.model.*;
import com.grupo3.logitrack_backend.repository.EnvioRepository;
import com.grupo3.logitrack_backend.repository.HistorialEstadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")   // ← Add this line
class EnvioServiceTest {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private HistorialEstadoRepository historialEstadoRepository;

    @BeforeEach
    void setUp() {
        historialEstadoRepository.deleteAll();
        envioRepository.deleteAll();
    }

    // ==================== YOUR TEST METHODS ====================

    @Test
    void crearEnvio_deberiaGuardarEnvioConDatosCorrectos() {
        Envio envio = crearEnvioValido();
        Envio saved = envioService.crearEnvio(envio);

        assertNotNull(saved.getId());
        assertNotNull(saved.getTrackingId());
        assertTrue(saved.getTrackingId().startsWith("LT-"));
        assertEquals(EstadoEnvio.PENDIENTE, saved.getEstadoEnvio());
    }

    @Test
    void crearEnvio_deberiaLanzarExcepcionCuandoDireccionEsInvalida() {
        Envio envio = crearEnvioValido();
        envio.setOrigen("corta");

        assertThrows(IllegalArgumentException.class, () -> envioService.crearEnvio(envio));
    }

    @Test
    void actualizarEstadoEnvio_deberiaCambiarEstadoYRegistrarHistorial() {
        Envio envio = crearEnvioValido();
        Envio saved = envioService.crearEnvio(envio);
        String trackingId = saved.getTrackingId();

        Optional<Envio> resultado = envioService.actualizarEstadoEnvio(
                trackingId, EstadoEnvio.EN_VIAJE, "Paquete recogido", "operador1");

        assertTrue(resultado.isPresent());
        assertEquals(EstadoEnvio.EN_VIAJE, resultado.get().getEstadoEnvio());

        List<HistorialEstado> historial = envioService.verHistorialEstado(trackingId);
        assertEquals(1, historial.size());
    }

    @Test
    void actualizarEstadoEnvio_deberiaLanzarExcepcionEnTransicionInvalida() {
        Envio envio = crearEnvioValido();
        Envio saved = envioService.crearEnvio(envio);

        assertThrows(IllegalStateException.class, () ->
                envioService.actualizarEstadoEnvio(saved.getTrackingId(), EstadoEnvio.ENTREGADO, "inválido", "test"));
    }

    @Test
    void calcularMetricas_deberiaCalcularPorcentajesCorrectamente() {
    // Create 4 shipments
    Envio e1 = crearEnvioValido(); 
    envioService.crearEnvio(e1);                    // remains PENDIENTE

    // e2 → PENDIENTE → EN_VIAJE
    Envio e2 = crearEnvioValido();
    Envio saved2 = envioService.crearEnvio(e2);
    envioService.actualizarEstadoEnvio(saved2.getTrackingId(), EstadoEnvio.EN_VIAJE, "Recogido por transportista", "operador");

    // e3 → PENDIENTE → EN_VIAJE → ENTREGADO
    Envio e3 = crearEnvioValido();
    Envio saved3 = envioService.crearEnvio(e3);
    envioService.actualizarEstadoEnvio(saved3.getTrackingId(), EstadoEnvio.EN_VIAJE, "En camino", "operador");
    envioService.actualizarEstadoEnvio(saved3.getTrackingId(), EstadoEnvio.ENTREGADO, "Entregado exitosamente", "repartidor");

    // e4 → PENDIENTE → CANCELADO
    Envio e4 = crearEnvioValido();
    Envio saved4 = envioService.crearEnvio(e4);
    envioService.actualizarEstadoEnvio(saved4.getTrackingId(), EstadoEnvio.CANCELADO, "Cliente canceló", "admin");

    // Calculate metrics
    MetricasDTO metricas = envioService.calcularMetricas();

    // Assertions
    assertEquals(4, metricas.getTotalEnvios());

    assertEquals(25.0, metricas.getPorcentajePendientes(), 0.01);   // e1
    assertEquals(25.0, metricas.getPorcentajeEnTransito(), 0.01);   // e2
    assertEquals(25.0, metricas.getPorcentajeEntregados(), 0.01);   // e3
    assertEquals(25.0, metricas.getPorcentajeCancelados(), 0.01);   // e4

    double sumaPorcentajes = metricas.getPorcentajePendientes()
            + metricas.getPorcentajeEnTransito()
            + metricas.getPorcentajeEntregados()
            + metricas.getPorcentajeCancelados();

    assertEquals(100.0, sumaPorcentajes, 0.01);
}

    @Test
    void calcularMetricas_deberiaRetornarCerosCuandoNoExistenEnvios() {
        MetricasDTO metricas = envioService.calcularMetricas();

        assertEquals(0, metricas.getTotalEnvios());
        assertEquals(0.0, metricas.getPorcentajePendientes(), 0.001);
        assertEquals(0.0, metricas.getPorcentajeEnTransito(), 0.001);
        assertEquals(0.0, metricas.getPorcentajeEntregados(), 0.001);
        assertEquals(0.0, metricas.getPorcentajeCancelados(), 0.001);
    }

    @Test
    void verHistorialEstado_deberiaRetornarListaOrdenada() {
        Envio envio = crearEnvioValido();
        Envio saved = envioService.crearEnvio(envio);
        String trackingId = saved.getTrackingId();

        envioService.actualizarEstadoEnvio(trackingId, EstadoEnvio.EN_VIAJE, "Recogido", "user1");
        envioService.actualizarEstadoEnvio(trackingId, EstadoEnvio.ENTREGADO, "Entregado", "user2");

        List<HistorialEstado> historial = envioService.verHistorialEstado(trackingId);
        assertEquals(2, historial.size());
        assertEquals(EstadoEnvio.ENTREGADO, historial.get(0).getEstadoNuevo());
    }

    // ==================== HELPER ====================
    private Envio crearEnvioValido() {
        Envio envio = new Envio();
        envio.setOrigen("Av. Principal 1234, Ciudad Central");
        envio.setDestino("Calle Secundaria 567, Barrio Norte");
        envio.setTipoEnvio(TipoEnvio.NORMAL);
        envio.setSaturacion(Saturacion.MEDIA);
        envio.setDistanciaEstimada(850);
        envio.setVolumen(8);
        envio.setVentanaHoras(36);
        envio.setFragil(true);
        envio.setFrio(false);
        return envio;
    }
}