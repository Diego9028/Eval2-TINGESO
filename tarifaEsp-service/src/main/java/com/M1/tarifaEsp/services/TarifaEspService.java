package com.M1.tarifaEsp.services;

import com.M1.tarifaEsp.entities.TarifaEsp;
import com.M1.tarifaEsp.repositories.TarifaEspRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

@Service
public class TarifaEspService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TarifaEspRepository tarifaEspRepository;

    public int obtenerDescuento(Long clienteId, LocalDate fecha) {
        // 1. Cumpleaños
        Map<String, Object> response = restTemplate.getForObject(
                "http://cliente-service/api/clientes/" + clienteId + "/fecha-nacimiento",
                Map.class
        );

        int dCumple = 0;
        if (response != null && response.containsKey("fechaNacimiento")) {
            String fechaNacStr = response.get("fechaNacimiento").toString();
            LocalDate fechaNacimiento = LocalDate.parse(fechaNacStr);
            if (fecha.getDayOfMonth() == fechaNacimiento.getDayOfMonth() &&
                    fecha.getMonth() == fechaNacimiento.getMonth()) {
                dCumple = 50;
            }
        }

        // 2. Fin de semana
        int dFinde = (fecha.getDayOfWeek() == DayOfWeek.SATURDAY ||
                fecha.getDayOfWeek() == DayOfWeek.SUNDAY) ? 10 : 0;

        // 3. Fecha especial (feriado)
        TarifaEsp fechaEspecial = tarifaEspRepository.findByFechaEspecial(fecha);
        int dFeriado = (fechaEspecial != null) ? 20 : 0;

        // 4. Mayor descuento aplica
        return Math.max(dCumple, Math.max(dFinde, dFeriado));
    }
}
