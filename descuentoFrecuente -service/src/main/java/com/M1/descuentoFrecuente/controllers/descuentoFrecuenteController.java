package com.M1.descuentoFrecuente.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/descuento-frecuencia")
public class descuentoFrecuenteController {

    @Autowired
    private com.M1.descuentoFrecuente.services.descuentoFrecuenteService service;

    @GetMapping
    public int obtenerDescuento(@RequestParam int Frecuencia) {
        return service.calcularDescuentoPorFrecuencia(Frecuencia);
    }
}
