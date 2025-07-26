package com.M1.rack.entities;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RackSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int anio;

    private int numeroSemana;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @ElementCollection
    private List<Long> idsReservas;
}
