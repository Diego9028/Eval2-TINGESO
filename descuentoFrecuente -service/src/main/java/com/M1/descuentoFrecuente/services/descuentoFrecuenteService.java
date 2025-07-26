package com.M1.descuentoFrecuente.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class descuentoFrecuenteService {

    @Autowired
    private com.M1.descuentoFrecuente.repositories.descuentoFrecuenteRepository repository;

    public int calcularDescuentoPorFrecuencia(int FrecuenciaPersonas) {
        return repository.findAll().stream()
                .filter(d -> FrecuenciaPersonas >= d.getMinVisitas() && FrecuenciaPersonas <= d.getMaxVisitas())
                .mapToInt(com.M1.descuentoFrecuente.entities.descuentoFrecuente::getPorcentaje)
                .findFirst()
                .orElse(0);
    }


}

