package com.blackmesaresearch.hytrac.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.dto.request.GenerarCotRequestDTO;
import com.blackmesaresearch.hytrac.dto.response.CotResponseDTO;


@Service
public class ArbaService {


    // =========================
    // GENERAR COT
    // =========================

    public CotResponseDTO generarCot(
            GenerarCotRequestDTO dto) {


        // =========================
        // VALIDACIONES SIMULADAS ARBA
        // =========================


        if(dto.origen() == null ||
           dto.origen().isBlank() ||
           dto.destino() == null ||
           dto.destino().isBlank()) {

            throw new IllegalArgumentException(
                    "Origen y destino son obligatorios para generar COT.");
        }



        if(dto.litrosCargados() == null ||
           dto.litrosCargados() <= 0) {

            throw new IllegalArgumentException(
                    "Los litros cargados son obligatorios.");
        }



        if(dto.densidad() == null ||
           dto.densidad() <= 0) {

            throw new IllegalArgumentException(
                    "La densidad del producto es obligatoria.");
        }



        if(dto.valorMercaderia() == null ||
           dto.valorMercaderia() <= 0) {

            throw new IllegalArgumentException(
                    "El valor de mercadería es obligatorio.");
        }



        if(dto.producto() == null ||
           dto.producto().isBlank()) {

            throw new IllegalArgumentException(
                    "El producto es obligatorio.");
        }



        // =========================
        // CALCULAR PESO
        // =========================

        Double pesoCarga =
                dto.litrosCargados()
                *
                dto.densidad();



        // =========================
        // VALIDACION PESO ARBA
        // =========================

        if(pesoCarga <= 0) {

            throw new IllegalArgumentException(
                    "El peso calculado no es válido.");
        }



        // =========================
        // GENERAR COT
        // =========================

        String codigoCot =
                generarCodigoCot();



        return new CotResponseDTO(

                codigoCot,

                "COT generado correctamente",

                LocalDateTime.now()
        );

    }





    // =========================
    // GENERADOR CODIGO COT
    // =========================

    private String generarCodigoCot() {


        Random random = new Random();


        int numero =
                100000000 +
                random.nextInt(900000000);



        return "COT-" + numero;

    }

}