package com.M1.rack.services;


import com.M1.rack.entities.RackSemanal;
import com.M1.rack.repositories.RackSemanalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RackSemanalService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RackSemanalRepository repository;

    public List<Map<String, Object>> obtenerReservasDeSemana(int anio, int semana) {
        // Si ya está registrada, la reutilizamos
        Optional<RackSemanal> existente = repository.findByAnioAndNumeroSemana(anio, semana);
        if (existente.isPresent()) {
            List<Long> ids = existente.get().getIdsReservas();
            return obtenerDetallesDesdeReservaService(ids);
        }

        // Calcular inicio y fin de la semana
        LocalDate inicio = LocalDate.of(anio, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, semana)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fin = inicio.plusDays(6);

        String url = "http://reserva-service/api/reservas/por-fecha?inicio=" + inicio.atStartOfDay() + "&fin=" + fin.atTime(23, 59, 59);
        List<Map<String, Object>> reservas = restTemplate.getForObject(url, List.class);

        // Guardar en RackSemanal
        List<Long> ids = reservas.stream()
                .map(r -> Long.valueOf(((Number)((Map<?, ?>) r).get("id")).toString()))
                .toList();

        RackSemanal rack = new RackSemanal(null, anio, semana, inicio, fin, ids);
        repository.save(rack);

        return reservas;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> obtenerDetallesDesdeReservaService(List<Long> ids) {
        return ids.stream()
                .map(id -> {
                    try {
                        return (Map<String, Object>) restTemplate.getForObject("http://reserva-service/api/reservas/" + id, Map.class);
                    } catch (HttpClientErrorException.NotFound e) {
                        // Reserva no encontrada → ignorar
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }




    public void removerReservaDeTodosLosRacks(Long reservaId) {
        List<RackSemanal> racks = repository.findAll();

        for (RackSemanal rack : racks) {
            if (rack.getIdsReservas().contains(reservaId)) {
                rack.getIdsReservas().remove(reservaId);
                repository.save(rack);
            }
        }
    }

}

