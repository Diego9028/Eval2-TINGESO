package com.M1.descuentoFrecuente.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface descuentoFrecuenteRepository extends JpaRepository<com.M1.descuentoFrecuente.entities.descuentoFrecuente, Long> {
    List<com.M1.descuentoFrecuente.entities.descuentoFrecuente> findAll();
}
