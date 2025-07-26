package com.M1.karts.services;

import com.M1.karts.entities.Kart;
import com.M1.karts.repositories.KartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KartService {

    private final KartRepository kartRepository;

    public KartService(KartRepository kartRepository) {
        this.kartRepository = kartRepository;
    }

    public List<Kart> obtenerTodos() {
        return kartRepository.findAll();
    }
}
