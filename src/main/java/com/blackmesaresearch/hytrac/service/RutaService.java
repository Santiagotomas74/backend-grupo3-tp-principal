package com.blackmesaresearch.hytrac.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.blackmesaresearch.hytrac.dto.graphhopper.GraphhopperPoints;
import com.blackmesaresearch.hytrac.dto.graphhopper.GraphhopperResponse;
import com.blackmesaresearch.hytrac.dto.response.RutaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.LugarOperativo;
import com.blackmesaresearch.hytrac.model.core.Ruta;
import com.blackmesaresearch.hytrac.repository.LugarOperativoRepository;
import com.blackmesaresearch.hytrac.repository.RutaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class RutaService {

    private static final Logger logger = LoggerFactory.getLogger(RutaService.class);

    private final RutaRepository rutaRepository;
    private final LugarOperativoRepository lugarOperativoRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${graphhopper.api.key}")
    private String graphhopperApiKey;

    @Value("${graphhopper.api.url:https://graphhopper.com/api/1/route}")
    private String graphhopperApiUrl;

    public RutaService(
            RutaRepository rutaRepository,
            LugarOperativoRepository lugarOperativoRepository,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.rutaRepository = rutaRepository;
        this.lugarOperativoRepository = lugarOperativoRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Calcula o obtiene una ruta entre dos ubicaciones operativas
     * Si la ruta ya existe en la BD, retorna los datos guardados
     * Si no existe, calcula mediante GraphHopper API, guarda y retorna
     *
     * @param origenId  ID del lugar operativo de origen
     * @param destinoId ID del lugar operativo de destino
     * @return RutaResponseDTO con distancia, tiempo y geometría
     * @throws IllegalArgumentException si origen o destino no existen
     */
    public RutaResponseDTO calcularRuta(Integer origenId, Integer destinoId) {
        // 1. Obtener origen y destino de la BD (error si no existen)
        LugarOperativo origen = lugarOperativoRepository.findById(origenId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lugar operativo origen no encontrado con ID: " + origenId));

        LugarOperativo destino = lugarOperativoRepository.findById(destinoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lugar operativo destino no encontrado con ID: " + destinoId));

        // Validar que tengan coordenadas
        if (origen.getLatitud() == null || origen.getLongitud() == null) {
            throw new IllegalArgumentException(
                    "Lugar operativo origen no tiene coordenadas válidas");
        }
        if (destino.getLatitud() == null || destino.getLongitud() == null) {
            throw new IllegalArgumentException(
                    "Lugar operativo destino no tiene coordenadas válidas");
        }

        // 2. Buscar si la ruta ya existe
        Optional<Ruta> rutaExistente = rutaRepository.findByOrigenIdAndDestinoId(
                origenId, destinoId);

        if (rutaExistente.isPresent()) {
            // 3a. Si existe, retornar datos almacenados
            return toResponseDTO(rutaExistente.get());
        }

        // 3b. Si no existe, calcular mediante API y guardar
        Ruta rutaNueva = calcularYGuardarRuta(origen, destino);
        return toResponseDTO(rutaNueva);
    }

    /**
     * Calcula una ruta mediante GraphHopper API y la guarda en la BD
     */
    private Ruta calcularYGuardarRuta(LugarOperativo origen, LugarOperativo destino) {
        // Llamar a GraphHopper API
        GraphhopperResponse response = llamarGraphhopperAPI(origen, destino);

        if (response == null || response.routes() == null || response.routes().isEmpty()) {
            String errorMsg = String.format(
                    "No se encontró ruta en la respuesta de GraphHopper para origen=%d (%s, %s) a destino=%d (%s, %s)",
                    origen.getId(), origen.getLatitud(), origen.getLongitud(),
                    destino.getId(), destino.getLatitud(), destino.getLongitud());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // Obtener la primera ruta (mejor ruta)
        var ruta = response.routes().get(0);

        // Convertir datos a las unidades correctas
        // GraphHopper retorna distancia en metros, nosotros la guardamos en km
        Double distanciaKm = ruta.distance() / 1000.0;

        // GraphHopper retorna tiempo en milisegundos, nosotros lo guardamos en horas
        // Asumimos que el camion se mueve a un 73% de la velocidad de un auto.
        // Graphopper devuelve para una velocidad promedio de 110 km/h para autos. Esto
        // equivale a un incremento de 1.375 en el tiempo estimado para camiones.
        Double tiempoHoras = (ruta.time() / (1000.0 * 3600.0)) * 1.375; // Ajuste de velocidad para camiones

        // Convertir points a JSON
        String geometriaJson = convertPointsToJson(ruta.points());

        // Crear nueva entidad Ruta
        Ruta rutaNueva = new Ruta();
        rutaNueva.setOrigen(origen);
        rutaNueva.setDestino(destino);
        rutaNueva.setDistanciaKm(distanciaKm);
        rutaNueva.setTiempoEstimadoHoras(tiempoHoras);
        rutaNueva.setGeometriaJson(geometriaJson);

        // Guardar en BD
        return rutaRepository.save(rutaNueva);
    }

    /**
     * Llama a la API de GraphHopper para obtener ruta
     */
    private GraphhopperResponse llamarGraphhopperAPI(
            LugarOperativo origen,
            LugarOperativo destino) {

        // Verificar que tengamos API key
        if (graphhopperApiKey == null || graphhopperApiKey.isBlank()) {
            logger.error("GraphHopper API key no está configurada. Verifica application-local.properties");
            throw new RuntimeException("GraphHopper API key no está configurada");
        }

        String url = UriComponentsBuilder.fromHttpUrl(graphhopperApiUrl)
                .queryParam("point", origen.getLatitud() + "," + origen.getLongitud())
                .queryParam("point", destino.getLatitud() + "," + destino.getLongitud())
                .queryParam("profile", "car")
                .queryParam("locale", "es")
                .queryParam("calc_points", "true")
                .queryParam("points_encoded", "false")
                .queryParam("key", graphhopperApiKey)
                .toUriString();

        System.out.println("URL de GraphHopper API: " + url);

        logger.info("Llamando a GraphHopper API: origen={} {} a destino={} {}",
                origen.getId(), origen.getNombre(),
                destino.getId(), destino.getNombre());

        try {
            GraphhopperResponse response = restTemplate.getForObject(url, GraphhopperResponse.class);
            if (response == null) {
                logger.error("Respuesta nula de GraphHopper API");
                throw new RuntimeException("Respuesta nula de GraphHopper API");
            }

            logger.info("Respuesta de GraphHopper recibida con {} rutas",
                    response.routes() != null ? response.routes().size() : 0);

            return response;
        } catch (Exception e) {
            logger.error("Error al llamar a GraphHopper API: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "Error al llamar a GraphHopper API: " + e.getMessage(), e);
        }
    }

    /**
     * Convierte la lista de puntos a formato JSON
     */
    private String convertPointsToJson(java.util.List<java.util.List<Double>> points) {
        try {
            return objectMapper.writeValueAsString(points);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar puntos a JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Convierte una entidad Ruta a RutaResponseDTO
     */
    private RutaResponseDTO toResponseDTO(Ruta ruta) {
        return new RutaResponseDTO(
                ruta.getId(),
                ruta.getDistanciaKm(),
                ruta.getTiempoEstimadoHoras(),
                RutaResponseDTO.parseGeometria(ruta.getGeometriaJson()));
    }

    private String convertPointsToJson(GraphhopperPoints points) {
        try {
            // This converts the GraphhopperPoints object directly into a JSON string
            return objectMapper.writeValueAsString(points);
        } catch (Exception e) {
            logger.error("Error converting geometry points to JSON", e);
            return "{}";
        }
    }

}
