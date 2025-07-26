package com.M1.DescuentoPorCantidad.controllers;

import com.M1.DescuentoPorCantidad.services.DescuentoPorCantidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/descuento-personas")
public class DescuentoPorCantidadController {

    @Autowired
    private DescuentoPorCantidadService service;

    @GetMapping
    public int obtenerDescuento(@RequestParam int cantidad) {
        return service.calcularDescuentoPorCantidad(cantidad);
    }
}
