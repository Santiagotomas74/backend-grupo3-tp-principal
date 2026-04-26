package com.grupo3.logitrack_backend.service;

import org.springframework.stereotype.Service;

import com.grupo3.logitrack_backend.model.Prioridad;

import smile.classification.RandomForest;
import java.io.*;

@Service
public class PrioridadService {
  private RandomForest modelo;

  public PrioridadService() {
    try {
      InputStream modeloStream = getClass().getResourceAsStream("/priorityModel.ser");

      if (modeloStream == null) {
        throw new RuntimeException("No se encontró priorityModel.ser en resources");
      }

      try (ObjectInputStream ois = new ObjectInputStream(modeloStream)) {
        modelo = (RandomForest) ois.readObject();
      }

      System.out.println("Modelo ML cargado correctamente desde priorityModel.ser");

    } catch (Exception e) {
      throw new RuntimeException("Error cargando modelo ML", e);
    }
  }

  public Prioridad predecirPrioridad(int distanciaKm, int tipoEnvio,
      int ventanaHoras, int volumen,
      boolean fragil, boolean frio,
      int saturacionInt) {

    if (modelo == null) {
      System.err.println("Modelo ML no disponible, usando prioridad MEDIA por defecto");
      return Prioridad.MEDIA;
    }

    Object[] valores = {
        distanciaKm,
        tipoEnvio,
        ventanaHoras,
        volumen,
        fragil ? 1 : 0,
        frio ? 1 : 0,
        saturacionInt
    };

    smile.data.Tuple tuple = smile.data.Tuple.of(modelo.schema(), valores);

    int prediccion = modelo.predict(tuple);

    return Prioridad.fromCode(prediccion);
  }

}
