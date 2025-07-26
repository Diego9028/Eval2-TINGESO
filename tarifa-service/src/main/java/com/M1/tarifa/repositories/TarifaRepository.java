package com.M1.tarifa.repositories;

import com.M1.tarifa.entities.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    Tarifa findByDuracionTotalMinutos(int duracionTotalMinutos);
    Tarifa findByNumeroVueltas(int numeroVueltas);

}
