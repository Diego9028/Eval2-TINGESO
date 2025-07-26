package com.M1.reserva.services;

import com.M1.reserva.entities.Comprobante;
import com.M1.reserva.entities.Reserva;
import com.M1.reserva.model.Kart;
import com.M1.reserva.repositories.ComprobanteRepository;
import com.M1.reserva.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ReservaService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReservaRepository reservaRepository;


    public Reserva crearReserva(Reserva nuevaReserva) {
        // 🧩 Paso 0: Obtener IDs de clientes desde correos
        Long titularId = obtenerClienteIdPorCorreo(nuevaReserva.getCorreoTitular());

        List<Long> idsParticipantes = nuevaReserva.getCorreosParticipantes().stream()
                .map(this::obtenerClienteIdPorCorreo)
                .toList();

        nuevaReserva.setClienteTitularId(titularId);
        nuevaReserva.setIdsClientesReserva(idsParticipantes);
        nuevaReserva.setCantidadPersonas(idsParticipantes.size());

        // frecuencia de reservas del titular
        int reservasEsteMes = obtenerReservasDelMes(titularId, nuevaReserva.getFechaHoraReserva());


        // precios
        int precioBase = obtenerPrecioBase(nuevaReserva.getNumeroVueltas());

    // obtener descuentos disponibles
        int descuentoEspecial = obtenerDescuentoEspecial(titularId, nuevaReserva.getFechaHoraReserva().toLocalDate());
        int descuentoPorCantidad = obtenerDescuentoPorCantidad(nuevaReserva.getCantidadPersonas());
        int descuentoPorFrecuencia = obtenerDescuentoPorFrecuencia(reservasEsteMes);

    // aplicar solo el mejor descuento
        int mejorDescuento = Math.max(descuentoEspecial, Math.max(descuentoPorCantidad, descuentoPorFrecuencia));
        int precioConDescuento = precioBase - (precioBase * mejorDescuento / 100);
        // aplicar IVA
        int precioConIva = (int) Math.round(precioConDescuento * 1.19);

        nuevaReserva.setPrecioBase(precioBase);
        nuevaReserva.setPrecioFinal(precioConIva);

        // 🧩 Paso 1: Calcular hora de fin desde tarifa-service
        LocalDateTime inicio = nuevaReserva.getFechaHoraReserva();
        LocalDateTime fin = calcularHoraFin(nuevaReserva.getNumeroVueltas(), inicio);
        nuevaReserva.setFechaHoraFin(fin);



        // 🧩 Paso 2: Verificar karts disponibles
        List<Reserva> reservasSolapadas = reservaRepository.findReservasSolapadas(inicio, fin);


        List<Long> idsOcupados = reservasSolapadas.stream()
                .flatMap(r -> r.getIdsKartsReservados().stream())
                .distinct()
                .toList();

        Kart[] todosKarts = obtenerTodosLosKarts();
        List<Kart> kartsDisponibles = Arrays.stream(todosKarts)
                .filter(k -> !idsOcupados.contains(k.getId()))
                .toList();


        if (kartsDisponibles.size() < nuevaReserva.getCantidadPersonas()) {
            throw new IllegalStateException("No hay suficientes karts disponibles para esta reserva.");
        }

        // 🧩 Paso 3: Asignar karts disponibles
        List<Long> idsAsignados = kartsDisponibles.subList(0, nuevaReserva.getCantidadPersonas()).stream()
                .map(Kart::getId)
                .toList();

        nuevaReserva.setIdsKartsReservados(idsAsignados);


        nuevaReserva.setEstado("CONFIRMADA");

        // 🧩 Paso 4: Guardar reserva

        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);
        crearComprobante(reservaGuardada, mejorDescuento);
        return reservaGuardada;
    }


    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    private LocalDateTime calcularHoraFin(int numeroVueltas, LocalDateTime inicio) {
        String url = "http://tarifa-service/api/tarifas/by-vueltas?numeroVueltas=" + numeroVueltas;
        Map<String, Object> tarifa = restTemplate.getForObject(url, Map.class);

        if (tarifa != null && tarifa.containsKey("duracionTotalMinutos")) {
            int duracion = Integer.parseInt(tarifa.get("duracionTotalMinutos").toString());
            return inicio.plusMinutes(duracion);
        } else {
            throw new IllegalStateException("No se pudo obtener la duración desde tarifa-service.");
        }
    }

    private int obtenerPrecioBase(int numeroVueltas) {
        String url = "http://tarifa-service/api/tarifas/precio?NumeroVueltas=" + numeroVueltas;
        Integer precio = restTemplate.getForObject(url, Integer.class);
        if (precio != null) return precio;
        throw new IllegalStateException("No se pudo obtener el precio desde tarifa-service.");
    }



    private Long obtenerClienteIdPorCorreo(String correo) {
        String url = "http://cliente-service/api/clientes/email?email=" + correo;
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("id")) {
                return Long.valueOf(response.get("id").toString());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo obtener cliente con correo: " + correo);
        }
        throw new IllegalArgumentException("Cliente no encontrado: " + correo);
    }

    private Kart[] obtenerTodosLosKarts() {
        return restTemplate.getForObject("http://karts-service/api/karts", Kart[].class);
    }

    private int obtenerDescuentoEspecial(Long clienteId, LocalDate fecha) {
        String url = "http://tarifaEsp-service/api/tarifa-especial/descuento"
                + "?clienteId=" + clienteId
                + "&fecha=" + fecha;
        try {
            Integer descuento = restTemplate.getForObject(url, Integer.class);
            return (descuento != null) ? descuento : 0;
        } catch (Exception e) {
            // puedes loguear el error o dejar 0 como fallback
            return 0;
        }
    }

    private int obtenerDescuentoPorCantidad(int cantidad) {
        String url = "http://descuentoPorCantidad-service/api/descuento-personas?cantidad=" + cantidad;
        try {
            Integer descuento = restTemplate.getForObject(url, Integer.class);
            return (descuento != null) ? descuento : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int obtenerDescuentoPorFrecuencia(int Frecuencia) {
        String url = "http://descuentoFrecuente-service/api/descuento-frecuencia?Frecuencia=" + Frecuencia;
        try {
            Integer descuento = restTemplate.getForObject(url, Integer.class);
            return (descuento != null) ? descuento : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int obtenerReservasDelMes(Long clienteId, LocalDateTime fecha) {
        LocalDateTime inicioMes = fecha.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = inicioMes.plusMonths(1).minusNanos(1);

        return reservaRepository.contarReservasPorMes(clienteId, inicioMes, finMes);
    }

    // COMPROBANTE
    @Autowired
    private ComprobanteRepository comprobanteRepository;

    @Autowired
    private ComprobanteService comprobanteService; // para enviar por correo

    public Comprobante crearComprobante(Reserva reserva, int mejorDescuento) {
        // Obtener nombres
        String nombreTitular = obtenerNombrePorId(reserva.getClienteTitularId());
        List<String> nombresParticipantes = reserva.getIdsClientesReserva().stream()
                .map(this::obtenerNombrePorId)
                .toList();

        // Cálculos
        int tarifaBase = reserva.getPrecioBase();
        int subtotal = tarifaBase - (tarifaBase * mejorDescuento / 100);
        int iva = (int) Math.round(subtotal * 0.19);
        int total = subtotal + iva;

        // Crear comprobante
        Comprobante comprobante = new Comprobante();
        comprobante.setReserva(reserva);
        comprobante.setNombreTitular(nombreTitular);
        comprobante.setNombresParticipantes(nombresParticipantes);
        comprobante.setTarifaBase(tarifaBase);
        comprobante.setDescuentoAplicado(mejorDescuento);
        comprobante.setSubtotal(subtotal);
        comprobante.setIva(iva);
        comprobante.setTotal(total);

        // Guardar
        comprobante = comprobanteRepository.save(comprobante);

        // Enviar correo
        try {
            comprobanteService.enviarComprobantePorEmail(comprobante);
        } catch (Exception e) {
            System.err.println("Error al enviar comprobante por correo: " + e.getMessage());
        }

        return comprobante;
    }

    private String obtenerNombrePorId(Long clienteId) {
        String url = "http://cliente-service/api/clientes/" + clienteId;
        try {
            Map<String, Object> cliente = restTemplate.getForObject(url, Map.class);
            return (cliente != null && cliente.containsKey("nombre"))
                    ? cliente.get("nombre").toString()
                    : "Desconocido";
        } catch (Exception e) {
            return "Desconocido";
        }
    }



}
