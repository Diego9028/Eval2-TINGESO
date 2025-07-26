import { Fragment, useCallback, useEffect, useState } from "react";
import axios from "../http-common";
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
import weekday from "dayjs/plugin/weekday";
import "dayjs/locale/es";

dayjs.extend(weekday);
dayjs.extend(isoWeek);
dayjs.locale("es");

const HOURS = Array.from({ length: 13 }, (_, i) => i + 10);
const DAYS = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

export default function RackSemanal() {
  const [primerDia, setPrimerDia] = useState(() =>
    dayjs().isoWeekday(1).startOf("day")
  );

  const [reservas, setReservas] = useState([]);
  const [tarifasCache, setTarifasCache] = useState({});
  const [nombresPorId, setNombresPorId] = useState({});
  const [tarifasListas, setTarifasListas] = useState(false);

  const fetchSemana = useCallback(async (lunes) => {
    const anio = lunes.year();
    const semana = lunes.isoWeek();

    try {
      const { data } = await axios.get("/api/rack-semanal", {
        params: { anio, semana }
      });

      setReservas(data);

      // Cargar nombres por ID
      for (const r of data) {
        await obtenerNombrePorId(r.idClienteTitular);
      }
    } catch (err) {
      console.error(err);
    }
  }, []);

  const obtenerNombrePorId = async (id) => {
    if (!id || nombresPorId[id]) return;

    try {
      const { data } = await axios.get(`/api/clientes/${id}`);
      if (data && data.nombre) {
        setNombresPorId(prev => ({ ...prev, [id]: data.nombre }));
      }
    } catch (err) {
      console.warn(`No se encontró cliente con ID: ${id}`);
    }
  };

  useEffect(() => {
    fetchSemana(primerDia);
  }, [primerDia, fetchSemana]);

  const fetchDuracionVueltas = async (vueltas) => {
    if (tarifasCache[vueltas]) return tarifasCache[vueltas];
    try {
      const { data } = await axios.get(`/api/tarifas/by-vueltas?numeroVueltas=${vueltas}`);
      const duracion = data.duracionTotalMinutos;
      setTarifasCache(prev => ({ ...prev, [vueltas]: duracion }));
      return duracion;
    } catch (err) {
      console.error("No se pudo obtener duración para", vueltas, "vueltas");
      return 0;
    }
  };

  useEffect(() => {
    const cargarDuraciones = async () => {
      const vueltasUnicas = [...new Set(reservas.map(r => r.numeroVueltas))];
      for (const v of vueltasUnicas) {
        await fetchDuracionVueltas(v);
      }
      setTarifasListas(true);
    };
    if (reservas.length > 0) cargarDuraciones();
  }, [reservas]);

  const reservasEnCelda = (dayIdx, hour) => {
    const cellStart = primerDia.clone().add(dayIdx, "day").hour(hour).minute(0);
    const cellEnd = cellStart.clone().add(1, "hour");

    return reservas.filter((r) => {
      const inicio = dayjs(r.fechaHoraReserva);
      const duracionMin = tarifasCache[r.numeroVueltas];
      if (!duracionMin || !inicio.isValid()) return false;

      const fin = inicio.add(duracionMin, "minute");

      return fin.isAfter(cellStart) && inicio.isBefore(cellEnd);
    });
  };

  const avanzarSemana = () => setPrimerDia(primerDia.add(7, "day"));
  const retrocederSemana = () => setPrimerDia(primerDia.subtract(7, "day"));

  const borrarReserva = async (reserva) => {
    if (!reserva) return;
    if (!window.confirm("¿Eliminar esta reserva?")) return;
    try {
      await axios.delete(`/api/reservas/eliminar/${reserva.id}`);
      alert("Reserva eliminada");
      setTarifasListas(false);
      fetchSemana(primerDia);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Error al eliminar");
    }
  };

  return (
    <div style={{ padding: "2rem" }}>
      <h2>Rack semanal</h2>

      <div style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: "1rem"
      }}>
        <button onClick={retrocederSemana}>← Semana anterior</button>
        <h3>{primerDia.format("DD/MM")} – {primerDia.clone().add(6, "day").format("DD/MM/YYYY")}</h3>
        <button onClick={avanzarSemana}>Semana siguiente →</button>
      </div>

      <div style={{
        display: "grid",
        gridTemplateColumns: "100px repeat(7, 1fr)",
        gap: "4px"
      }}>
        <div></div>
        {DAYS.map((d, i) => (
          <div key={i} style={{ fontWeight: "bold", textAlign: "center" }}>
            {d}<br />
            <span style={{ fontSize: "0.85rem" }}>
              {primerDia.clone().add(i, "day").format("DD/MM")}
            </span>
          </div>
        ))}

        {tarifasListas && HOURS.map(hour => (
          <Fragment key={hour}>
            <div style={{ fontWeight: "bold", textAlign: "center" }}>{hour}:00</div>
            {DAYS.map((_, dayIdx) => {
              const reservasEnHora = reservasEnCelda(dayIdx, hour);

              return (
                <div
                  key={`${dayIdx}-${hour}`}
                  style={{
                    height: "60px",
                    overflowY: "auto",
                    backgroundColor: reservasEnHora.length ? "#4dabf7" : "#e9ecef",
                    cursor: reservasEnHora.length ? "pointer" : "default",
                    textAlign: "center",
                    padding: "0.25rem",
                    fontSize: "0.75rem"
                  }}
                >
                  {reservasEnHora.map((reserva, idx) => (
                    <div
                      key={idx}
                      onClick={() => borrarReserva(reserva)}
                      style={{ marginBottom: "4px" }}
                      onMouseEnter={e => e.currentTarget.style.backgroundColor = "#f03e3e"}
                      onMouseLeave={e => e.currentTarget.style.backgroundColor = "transparent"}
                    >
                      <div style={{ fontWeight: "bold", fontSize: "0.7rem" }}>
                        {nombresPorId[reserva.idClienteTitular] || reserva.idClienteTitular}
                      </div>
                      <div style={{ fontSize: "0.65rem" }}>
                        {dayjs(reserva.fechaHoraReserva).format("HH:mm")}–
                        {dayjs(reserva.fechaHoraReserva)
                          .add(tarifasCache[reserva.numeroVueltas], "minute")
                          .format("HH:mm")}
                      </div>
                    </div>
                  ))}
                </div>
              );
            })}
          </Fragment>
        ))}
      </div>
    </div>
  );
}
