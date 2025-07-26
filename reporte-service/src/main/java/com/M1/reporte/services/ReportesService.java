package com.M1.reporte.services;

import com.M1.reporte.entities.Reportes;
import com.M1.reporte.repositories.ReportesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportesService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReportesRepository reportesRepo;

    public List<Reportes> generarIngresosPorVueltas(LocalDateTime inicio, LocalDateTime fin) {
        List<Map<String, Object>> reservas = obtenerReservas(inicio, fin);
        Map<Integer, Long> acumulados = new HashMap<>();

        for (Map<String, Object> r : reservas) {
            int vueltas = (int) r.get("numeroVueltas");
            int total = (int) r.get("precioFinal");
            acumulados.merge(vueltas, (long) total, Long::sum);
        }

        List<Reportes> resultado = acumulados.entrySet().stream()
                .map(e -> Reportes.builder()
                        .tipo("INGRESOS_VUELTAS")
                        .criterio(e.getKey() + " vueltas")
                        .totalIngresos(e.getValue())
                        .fechaGeneracion(LocalDateTime.now())
                        .build())
                .toList();

        return reportesRepo.saveAll(resultado);
    }

    public List<Reportes> generarIngresosPorPersonas(LocalDateTime inicio, LocalDateTime fin) {
        List<Map<String, Object>> reservas = obtenerReservas(inicio, fin);
        Map<String, Long> acumulados = new HashMap<>();

        for (Map<String, Object> r : reservas) {
            int cantidad = (int) r.get("cantidadPersonas");
            int total = (int) r.get("precioFinal");
            String rango = rangoPersonas(cantidad);
            acumulados.merge(rango, (long) total, Long::sum);
        }

        List<Reportes> resultado = acumulados.entrySet().stream()
                .map(e -> Reportes.builder()
                        .tipo("INGRESOS_PERSONAS")
                        .criterio(e.getKey())
                        .totalIngresos(e.getValue())
                        .fechaGeneracion(LocalDateTime.now())
                        .build())
                .toList();

        return reportesRepo.saveAll(resultado);
    }

    public List<Reportes> generarIngresosPorTiempo(LocalDateTime inicio, LocalDateTime fin) {
        List<Reportes> porVueltas = generarIngresosPorVueltas(inicio, fin);

        porVueltas.forEach(r -> {
            r.setTipo("INGRESOS_TIEMPO");
            String criterio = r.getCriterio(); // "10 vueltas"
            r.setCriterio(criterio.replace("vueltas", "minutos"));
        });

        return reportesRepo.saveAll(porVueltas);
    }



    private List<Map<String, Object>> obtenerReservas(LocalDateTime inicio, LocalDateTime fin) {
        String url = "http://reserva-service/api/reservas/por-fecha" +
                "?inicio=" + inicio + "&fin=" + fin;
        return restTemplate.getForObject(url, List.class);
    }

    private String rangoPersonas(int n) {
        if (n <= 2) return "1-2 personas";
        if (n <= 5) return "3-5 personas";
        if (n <= 10) return "6-10 personas";
        return "11-15 personas";
    }
}
