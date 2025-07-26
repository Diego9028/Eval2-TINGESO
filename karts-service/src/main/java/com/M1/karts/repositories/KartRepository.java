package com.M1.karts.repositories;

import com.M1.karts.entities.Kart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KartRepository extends JpaRepository<Kart, Long> {
}
