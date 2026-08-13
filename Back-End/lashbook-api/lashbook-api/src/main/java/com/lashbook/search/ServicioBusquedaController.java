package com.lashbook.search;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/busqueda/servicios")
public class ServicioBusquedaController {

    private final ServicioBusquedaService servicioBusquedaService;

    public ServicioBusquedaController(
        ServicioBusquedaService servicioBusquedaService
    ) {
        this.servicioBusquedaService = servicioBusquedaService;
    }

    @GetMapping
    public List<ServicioBusquedaDocument> buscar(
        @RequestParam(name = "q", defaultValue = "") String texto
    ) {
        return servicioBusquedaService.buscarPredictivo(texto);
    }
}