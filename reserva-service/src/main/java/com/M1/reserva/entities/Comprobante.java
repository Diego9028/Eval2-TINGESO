package com.M1.reserva.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la reserva
    @OneToOne
    @JoinColumn(name = "reserva_id", referencedColumnName = "id")
    private Reserva reserva;

    private String nombreTitular;

    @ElementCollection
    private List<String> nombresParticipantes;

    // Detalles de costos
    private int tarifaBase;
    private int descuentoAplicado; // en porcentaje
    private int subtotal;
    private int iva;
    private int total;
}

