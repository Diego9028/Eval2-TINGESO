package com.M1.DescuentoPorCantidad.services;


import com.M1.DescuentoPorCantidad.entities.DescuentoPorCantidad;
import com.M1.DescuentoPorCantidad.repositories.DescuentoPorCantidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DescuentoPorCantidadService {

    @Autowired
    private DescuentoPorCantidadRepository repository;

    public int calcularDescuentoPorCantidad(int cantidadPersonas) {
        return repository.findAll().stream()
                .filter(d -> cantidadPersonas >= d.getMinPersonas() && cantidadPersonas <= d.getMaxPersonas())
                .mapToInt(DescuentoPorCantidad::getPorcentaje)
                .findFirst()
                .orElse(0);
    }


}

