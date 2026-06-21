package com.blackmesaresearch.hytrac.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.blackmesaresearch.hytrac.model.core.Documentacion;
import com.blackmesaresearch.hytrac.repository.DocumentacionRepository;

@Service
public class DocumentacionService {


    private final DocumentacionRepository documentacionRepository;


    public DocumentacionService(
            DocumentacionRepository documentacionRepository) {

        this.documentacionRepository = documentacionRepository;
    }



    public void actualizarFechaVencimiento(
            Integer id,
            LocalDate nuevaFechaVencimiento) {


        // =========================
        // BUSCAR DOCUMENTO
        // =========================

        Documentacion documento =
                documentacionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Documento no encontrado."
                        )
                );



        // =========================
        // VALIDAR FECHA
        // =========================

        if(nuevaFechaVencimiento == null){

            throw new IllegalArgumentException(
                    "La fecha de vencimiento es obligatoria."
            );
        }



        // =========================
        // ACTUALIZAR
        // =========================

        documento.setFechaVencimiento(
                nuevaFechaVencimiento
        );



        // =========================
        // GUARDAR
        // =========================

        documentacionRepository.save(documento);

    }

}