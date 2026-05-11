package com.blackmesaresearch.hytrac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.blackmesaresearch.hytrac.model.core.OrdenCarga;
import com.blackmesaresearch.hytrac.service.OrdenCargaService;

@RestController
@RequestMapping("/ordenes")
@CrossOrigin("*")
public class OrdenCargaController {

    @Autowired
    private OrdenCargaService ordenCargaService;

    @GetMapping("/get")
    public List<OrdenCarga> obtenerOrdenes() {
        return ordenCargaService.obtenerTodas();
    }
}