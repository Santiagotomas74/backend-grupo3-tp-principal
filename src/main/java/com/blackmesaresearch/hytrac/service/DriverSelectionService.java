package com.blackmesaresearch.hytrac.service;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.ConductorOptimoRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.StatsTransportista;
import com.blackmesaresearch.hytrac.model.core.Transportista;

import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.StatsTransportistaRepository;

import smile.classification.RandomForest;
import smile.data.Tuple;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;

@Service
public class DriverSelectionService {

  @Autowired
  private CombustibleRepository combustibleRepository;
  @Autowired
  private TransportistaRepository transportistaRepository;
  @Autowired
  private StatsTransportistaRepository StatsTransportistaRepository;

  private RandomForest model;

  public DriverSelectionService() {
    loadModel();
  }

  // Cargar el modelo de ML a memoria
  private void loadModel() {
    String modelPath = "/ml/driverPredictionModel.ser";
    System.out.println("=== Loading Model to Memory ===");
    try (InputStream resourceStream = getClass().getResourceAsStream(modelPath)) {
      if (resourceStream == null) {
        throw new IllegalStateException("Model resource not found: " + modelPath);
      }
      try (ObjectInputStream ois = new ObjectInputStream(resourceStream)) {
        model = (RandomForest) ois.readObject();
        System.out.println("Model loaded successfully from: " + modelPath);
      }
    } catch (Exception e) {
      System.err.println("CRITICAL ERROR: Could not load the model file! " + e.getMessage());
      throw new IllegalStateException("Could not load SMILE model", e);
    }
  }

  public List<TransportistaResponseDTO> seleccionarTransportistaOptimo(ConductorOptimoRequestDTO request) {

    // 1. Estandarizar entrada

    int esCorta;
    int esMedia;
    int esLarga;

    if (request.tiempoEfectivoEstimadoHoras() < 4) {
      esCorta = 1;
      esMedia = 0;
      esLarga = 0;
    } else if (request.tiempoEfectivoEstimadoHoras() >= 4 && request.tiempoEfectivoEstimadoHoras() <= 8) {
      esCorta = 0;
      esMedia = 1;
      esLarga = 0;
    } else {
      esCorta = 0;
      esMedia = 0;
      esLarga = 1;
    }

    int esLiviana;
    int esPesada;

    Double densidad = combustibleRepository.findById(request.combustibleId())
        .map(c -> c.getDensidad())
        .orElse(1.0);

    // aun no se sabe cual es el peso del acoplado en si. por ahora se asume una
    // tara promedio de 8000kg, puesto que el vehiculo depende del conductor
    // seleccionado, que aun no tenemos.
    Double pesoCarga = request.volumenCargaLitros() * densidad + 8000;

    if (pesoCarga < 15000) {
      esLiviana = 1;
      esPesada = 0;
    } else {
      esLiviana = 0;
      esPesada = 1;
    }

    // 2. Obtener todos los transportistas disponibles
    List<Transportista> transportistasDisponibles = transportistaRepository.findAllByActivoTrueAndDisponibleTrue();

    // 3. Crear tabla de probabilidades de exito para cada transportista con esta
    // orden

    // Updated 13-Feature Schema Contract
    StructType schema = new StructType(new StructField[] {
        new StructField("experiencia_anios", DataTypes.DoubleType),
        new StructField("total_ordenes", DataTypes.DoubleType), // Added back
        new StructField("tasa_exito_total", DataTypes.DoubleType),
        new StructField("tasa_exito_largas", DataTypes.DoubleType),
        new StructField("tasa_exito_medias", DataTypes.DoubleType),
        new StructField("tasa_exito_cortas", DataTypes.DoubleType),
        new StructField("tasa_exito_pesadas", DataTypes.DoubleType),
        new StructField("tasa_exito_livianas", DataTypes.DoubleType),
        new StructField("es_larga", DataTypes.DoubleType),
        new StructField("es_media", DataTypes.DoubleType),
        new StructField("es_corta", DataTypes.DoubleType),
        new StructField("es_pesada", DataTypes.DoubleType),
        new StructField("es_liviana", DataTypes.DoubleType)
    });

    List<Pair<Transportista, Double>> probabilidadesExito = new ArrayList<>();

    for (Transportista t : transportistasDisponibles) {
      // matchear cada transportista con su estadistica para esta orden
      // (largo/media/corta, pesada/liviana) usando el modelo de ML y obtener la
      // probabilidad de exito

      // Buscamos sus stats
      StatsTransportista stats = StatsTransportistaRepository.findByTransportistaId(t.getId())
          .orElseThrow(() -> new IllegalStateException("Stats not found for transportista ID: " + t.getId()));

      double experienciaAnios = 10; // Placeholder, se debería obtener de alguna parte

      double totalOrdenes = stats.getTotalOrdenes() != null ? stats.getTotalOrdenes() : 0; // se supone que un condcutor
                                                                                           // novato tiene otro criterio
                                                                                           // de seleccion, por ahora le
                                                                                           // ponemos 100 hasta refinar.

      double tasaExitoTotal = totalOrdenes > 0
          ? (double) (stats.getLargasExitosas() + stats.getMediasExitosas() + stats.getCortasExitosas()) / totalOrdenes
          : 1.0;

      double tasaExitoLargas = stats.getLargas() > 0 ? (double) stats.getLargasExitosas() / stats.getLargas() : 1.0;
      double tasaExitoMedias = stats.getMediasExitosas() > 0
          ? (double) stats.getMediasExitosas() / stats.getMediasExitosas()
          : 1.0;
      double tasaExitoCortas = stats.getCortas() > 0 ? (double) stats.getCortasExitosas() / stats.getCortas() : 1.0;

      double tasaExitoPesadas = stats.getPesadas() > 0 ? (double) stats.getPesadasExitosas() / stats.getPesadas() : 1.0;
      double tasaExitoLivianas = stats.getLivianas() > 0 ? (double) stats.getLivianasExitosas() / stats.getLivianas()
          : 1.0;

      // Armamos vector de Features: [exp, total_orders, total_rate, long_rate,
      // med_rate, short_rate, heavy_rate, light_rate, es_larga, es_media, es_corta,
      // es_pesada, es_liviana]

      double[] features = {
          experienciaAnios,
          totalOrdenes,
          tasaExitoTotal,
          tasaExitoLargas,
          tasaExitoMedias,
          tasaExitoCortas,
          tasaExitoPesadas,
          tasaExitoLivianas,
          esLarga,
          esMedia,
          esCorta,
          esPesada,
          esLiviana
      };

      Tuple featureTuple = Tuple.of(schema, features);
      double[] probabilities = new double[2];
      model.predict(featureTuple, probabilities);
      double probabilidadExito = probabilities.length > 1 ? probabilities[1] : probabilities[0];

      // TODO:
      // 0.9 por ahora
      // aun no se considera si ninguno cumple
      if (probabilidadExito > 0.9) {
        probabilidadesExito.add(Pair.of(t, probabilidadExito));
      }
    }

    // ordenar de mayor a menor segun probabilidad
    probabilidadesExito.sort(Comparator.comparing(Pair::getRight));

    // devolver lista ordenada de transportistas optimos segun el modelo
    return probabilidadesExito.stream()
        .map(pair -> new TransportistaResponseDTO(pair.getLeft()))
        .toList();
  }

}
