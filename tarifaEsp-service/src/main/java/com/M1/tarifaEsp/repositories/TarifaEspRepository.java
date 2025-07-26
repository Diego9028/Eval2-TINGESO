package com.M1.tarifaEsp.repositories;

import com.M1.tarifaEsp.entities.TarifaEsp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TarifaEspRepository extends JpaRepository<TarifaEsp, Long> {
    TarifaEsp findByFechaEspecial(LocalDate fechaEspecial);
}
