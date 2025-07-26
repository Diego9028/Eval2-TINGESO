package com.M1.reserva.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kart {
    private Long id;
    private String codigo;
    private String modelo;
}

