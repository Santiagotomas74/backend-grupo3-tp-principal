package com.blackmesaresearch.hytrac.service;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.TransportistaOptimoRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.TransportistaResponseDTO;
import com.blackmesaresearch.hytrac.model.core.Transportista;
import com.blackmesaresearch.hytrac.model.stats.StatsTransportista;
import com.blackmesaresearch.hytrac.repository.TransportistaRepository;
import com.blackmesaresearch.hytrac.repository.CombustibleRepository;
import com.blackmesaresearch.hytrac.repository.StatsTransportistaRepository;

import smile.classification.RandomForest;
import smile.data.Tuple;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;

@Service
public class SeleccionTransportistasService {

  @Autowired
  private CombustibleRepository combustibleRepository;
  @Autowired
  private TransportistaRepository transportistaRepository;
  @Autowired
  private StatsTransportistaRepository StatsTransportistaRepository;

  private RandomForest model;

  public SeleccionTransportistasService() {
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

  public List<TransportistaResponseDTO> seleccionarTransportistasOptimos(TransportistaOptimoRequestDTO request) {

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

    System.out.println("=== SeleccionTransportistasOptimos ===");
    System.out.println("Orden details: tiempoHoras=" + request.tiempoEfectivoEstimadoHoras()
        + ", combustibleId=" + request.combustibleId()
        + ", volumenCargaLitros=" + request.volumenCargaLitros()
        + ", densidad=" + densidad
        + ", pesoCarga=" + pesoCarga
        + ", esCorta=" + esCorta
        + ", esMedia=" + esMedia
        + ", esLarga=" + esLarga
        + ", esLiviana=" + esLiviana
        + ", esPesada=" + esPesada);
    System.out.println("Transportistas disponibles: " + transportistasDisponibles.size());

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

    List<Pair<Transportista, Double>> probabilidadesExitoRegulares = new ArrayList<>();
    List<Transportista> novatos = new ArrayList<>();

    for (Transportista t : transportistasDisponibles) {

      // matchear cada transportista con su estadistica para esta orden
      // (largo/media/corta, pesada/liviana) usando el modelo de ML y obtener la
      // probabilidad de exito

      // Buscamos sus stats
      StatsTransportista stats = StatsTransportistaRepository.findByTransportistaId(t.getId())
          .orElseThrow(() -> new IllegalStateException("Stats not found for transportista ID: " + t.getId()));

      double experienciaAnios = t.getInicioActividad() != null
          ? ChronoUnit.YEARS.between(t.getInicioActividad(), LocalDate.now())
          : 0;

      double totalOrdenes = stats.getTotalOrdenes() != null ? stats.getTotalOrdenes() : 0; // se supone que un condcutor
                                                                                           // novato tiene otro criterio
                                                                                           // de seleccion, por ahora le
                                                                                           // ponemos 100 hasta refinar.

      // Si la orden es corta y el transportista es novato (pocas ordenes), entra de
      // manera especial
      if (totalOrdenes < 20 && esCorta == 1) {
        novatos.add(t);
        continue;
      }

      else {

        double tasaExitoTotal = totalOrdenes > 0
            ? (double) (stats.getLargasExitosas() + stats.getMediasExitosas() + stats.getCortasExitosas())
                / totalOrdenes
            : 1.0;

        double tasaExitoLargas = stats.getLargas() > 0 ? (double) stats.getLargasExitosas() / stats.getLargas() : 1.0;
        double tasaExitoMedias = stats.getMedias() > 0
            ? (double) stats.getMediasExitosas() / stats.getMedias()
            : 1.0;
        double tasaExitoCortas = stats.getCortas() > 0 ? (double) stats.getCortasExitosas() / stats.getCortas() : 1.0;

        double tasaExitoPesadas = stats.getPesadas() > 0 ? (double) stats.getPesadasExitosas() / stats.getPesadas()
            : 1.0;
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
          probabilidadesExitoRegulares.add(Pair.of(t, probabilidadExito));
        }

      }

    }

    // ordenar regulares segun probabilidad
    probabilidadesExitoRegulares.sort(
        Comparator.comparing(Pair::getRight, Comparator.reverseOrder()));

    // mezclar novatos
    Collections.shuffle(novatos);

    // armar array de resultado con todos los regulares
    List<Transportista> resultado = probabilidadesExitoRegulares.stream()
        .map(Pair::getLeft)
        .collect(Collectors.toCollection(ArrayList::new));

    // insertar hasta 10 novatos en posiciones aleatorias
    int cantidadNovatos = Math.min(10, novatos.size());

    Random random = new Random();

    for (int i = 0; i < cantidadNovatos; i++) {
      int posicion = random.nextInt(resultado.size() + 1);
      resultado.add(posicion, novatos.get(i));
    }

    // Imprimir transportistas seleccionados y sus probabilidades
    var probabilidadesByTransportistaId = probabilidadesExitoRegulares.stream()
        .collect(Collectors.toMap(p -> p.getLeft().getId(), Pair::getRight));

    System.out.println("=== Transportistas seleccionados ===");
    for (Transportista transportista : resultado) {
      String probabilityLabel = probabilidadesByTransportistaId.containsKey(transportista.getId())
          ? formatProbability(probabilidadesByTransportistaId.get(transportista.getId()))
          : "-1%";
      System.out.println("id=" + transportista.getId()
          + ", probability=" + probabilityLabel);
    }

    return resultado.stream()
        .map(t -> TransportistaResponseDTO.from(t,
            probabilidadesByTransportistaId.containsKey(t.getId())
                ? formatProbability(probabilidadesByTransportistaId.get(t.getId()))
                : "-1%"))
        .toList();
  }

  private String formatProbability(double probability) {
    double percent = Math.max(0.0, Math.min(100.0, probability * 100));
    return String.format("%.2f%%", percent);
  }

}
