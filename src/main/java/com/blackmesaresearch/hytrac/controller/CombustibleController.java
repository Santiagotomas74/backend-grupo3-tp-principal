package com.blackmesaresearch.hytrac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.blackmesaresearch.hytrac.dto.response.CombustibleResponseDTO;
import com.blackmesaresearch.hytrac.service.CombustibleService;
import java.util.List;

@RestController
@RequestMapping("/api/combustibles")
@CrossOrigin("*")
public class CombustibleController {

<<<<<<< HEAD
    @Autowired
    private CombustibleService combustibleService;
=======
    @Autowired private CombustibleService combustibleService;
>>>>>>> 5df270d33d9df5d9be29376f60cebc17b275eae7

    @GetMapping
    public ResponseEntity<List<CombustibleResponseDTO>> obtenerCombustibles() {
        return ResponseEntity.ok(combustibleService.obtenerCombustibles());
    }
}
