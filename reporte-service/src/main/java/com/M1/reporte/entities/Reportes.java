package com.M1.reporte.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reportes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String criterio;
    private Long totalIngresos;
    private LocalDateTime fechaGeneracion;
}
