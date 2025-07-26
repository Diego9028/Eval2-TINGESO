package com.M1.karts.controllers;

import com.M1.karts.entities.Kart;
import com.M1.karts.services.KartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/karts")
public class KartController {

    private final KartService kartService;

    public KartController(KartService kartService) {
        this.kartService = kartService;
    }

    @GetMapping
    public List<Kart> obtenerKarts() {
        return kartService.obtenerTodos();
    }
}
