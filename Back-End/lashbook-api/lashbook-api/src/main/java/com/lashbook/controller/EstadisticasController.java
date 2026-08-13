package com.lashbook.controller;

import com.lashbook.dto.EstadisticasResponse;
import com.lashbook.service.EstadisticasService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(
            EstadisticasService estadisticasService
    ) {
        this.estadisticasService = estadisticasService;
    }

    @GetMapping("/api/admin/estadisticas")
    public ResponseEntity<EstadisticasResponse>
    obtenerEstadisticas() {
        return ResponseEntity.ok(
            estadisticasService.obtenerEstadisticas()
        );
    }
}