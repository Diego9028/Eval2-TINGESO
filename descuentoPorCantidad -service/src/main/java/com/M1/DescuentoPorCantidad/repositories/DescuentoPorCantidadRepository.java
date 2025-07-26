package com.M1.DescuentoPorCantidad.repositories;

import com.M1.DescuentoPorCantidad.entities.DescuentoPorCantidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DescuentoPorCantidadRepository extends JpaRepository<DescuentoPorCantidad, Long> {
    List<DescuentoPorCantidad> findAll();
}
