package com.M1.reserva.controllers;

import com.M1.reserva.entities.Reserva;
import com.M1.reserva.repositories.ReservaRepository;
import com.M1.reserva.services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/crear")
    public Reserva crearReserva(@RequestBody Reserva reserva) {
        return reservaService.crearReserva(reserva);
    }

    @GetMapping("/todas")
    public List<Reserva> obtenerReservas() {
        return reservaService.obtenerTodas();
    }

    @GetMapping("/por-fecha")
    public List<Reserva> obtenerPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) {
        return reservaRepository.findByFechaHoraReservaBetween(inicio, fin);
    }

    @GetMapping("/{id}")
    public Map<String, Object> obtenerReservaPorId(@PathVariable Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        Map<String, Object> map = new HashMap<>();
        map.put("id", reserva.getId());
        map.put("cantidadPersonas", reserva.getCantidadPersonas());
        map.put("numeroVueltas", reserva.getNumeroVueltas());
        map.put("precioFinal", reserva.getPrecioFinal());
        map.put("fechaHoraReserva", reserva.getFechaHoraReserva().toString());
        map.put("idClienteTitular", reserva.getClienteTitularId());
        return map;
    }

    @DeleteMapping("/eliminar/{reservaId}")
    public void eliminarReserva(@PathVariable Long reservaId) {
        reservaRepository.deleteById(reservaId);

        // Notificar al rack-semana
        try {
            restTemplate.delete("http://rack-service/api/rack-semanal/remover-reserva/" + reservaId);
        } catch (Exception e) {
            System.err.println("Error notificando a rack-semana: " + e.getMessage());
        }
    }






}
