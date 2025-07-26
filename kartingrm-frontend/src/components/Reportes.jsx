import { useState } from "react";
import reportesService from "../services/reportes.service";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import "dayjs/locale/es";
dayjs.extend(isSameOrBefore);
dayjs.locale("es");

export default function Reportes() {
  const [inicioMes, setInicioMes] = useState(dayjs().startOf("month").format("YYYY-MM"));
  const [finMes, setFinMes] = useState(dayjs().format("YYYY-MM"));
  const [tipo, setTipo] = useState("vueltas");

  const [datos, setDatos] = useState({});
  const [meses, setMeses] = useState([]);
  const [cargando, setCargando] = useState(false);

  const generarListaMeses = (from, to) => {
    const lista = [];
    let cursor = from.clone();
    while (cursor.isSameOrBefore(to, "month")) {
      lista.push(cursor.clone());
      cursor = cursor.add(1, "month");
    }
    return lista;
  };

  const fetchDatos = async () => {
    setCargando(true);
    setDatos({});
    const ini = dayjs(inicioMes + "-01");
    const fin = dayjs(finMes + "-01");
    const listaMeses = generarListaMeses(ini, fin);
    setMeses(listaMeses.map(m => m.format("MMM YYYY")));

    const resultadosTemp = {};

    try {
      const responses = await Promise.all(
        listaMeses.map(async (mes) => {
          const inicioISO = mes.startOf("month").format("YYYY-MM-DD") + "T00:00:00";
          const finISO = mes.endOf("month").format("YYYY-MM-DD") + "T23:59:59";

          const fetchFn =
            tipo === "personas"
              ? reportesService.ingresosPersonas
              : reportesService.ingresosVueltas; // Único para vueltas y tiempo

          const { data } = await fetchFn(inicioISO, finISO);
          return { mes: mes.format("MMM YYYY"), data };
        })
      );

      for (const { mes, data } of responses) {
        data.forEach(r => {
          if (!resultadosTemp[r.criterio]) {
            resultadosTemp[r.criterio] = { total: 0 };
          }
          resultadosTemp[r.criterio][mes] = r.totalIngresos;
          resultadosTemp[r.criterio].total += r.totalIngresos;
        });
      }

      setDatos(resultadosTemp);
    } catch (err) {
      console.error("Error al obtener los datos:", err);
      alert("Hubo un error al generar el reporte. Intenta nuevamente.");
    }

    setCargando(false);
  };

  return (
    <div style={{ padding: "2rem" }}>
      <h2>Reportes de ingresos</h2>

      <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
        <div>
          <label>Inicio&nbsp;</label>
          <input type="month" value={inicioMes} onChange={e => setInicioMes(e.target.value)} />
        </div>
        <div>
          <label>Fin&nbsp;</label>
          <input type="month" value={finMes} onChange={e => setFinMes(e.target.value)} />
        </div>
        <div>
          <label>Tipo&nbsp;</label>
          <select value={tipo} onChange={e => setTipo(e.target.value)}>
            <option value="vueltas">Ingresos por vueltas o tiempo</option>
            <option value="personas">Ingresos por número de personas</option>
          </select>
        </div>
        <button onClick={fetchDatos} disabled={cargando}>
          {cargando ? "Cargando…" : "Generar Reporte"}
        </button>
      </div>

      {Object.keys(datos).length > 0 && (
        <div style={{ overflowX: "auto" }}>
          <table style={{ borderCollapse: "collapse", width: "100%" }}>
            <thead>
              <tr style={{ background: "#dee2e6" }}>
                <th style={{ padding: "0.5rem", border: "1px solid #ccc" }}>
                  {tipo === "personas"
                    ? "Número de personas"
                    : "Número de vueltas o minutos"}
                </th>
                {meses.map(m => (
                  <th key={m} style={{ padding: "0.5rem", border: "1px solid #ccc" }}>{m}</th>
                ))}
                <th style={{ padding: "0.5rem", border: "1px solid #ccc" }}>TOTAL</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(datos).map(([criterio, valores]) => (
                <tr key={criterio}>
                  <td style={{ padding: "0.5rem", border: "1px solid #ccc", fontWeight: "bold" }}>{criterio}</td>
                  {meses.map(m => (
                    <td key={m} style={{ padding: "0.5rem", border: "1px solid #ccc", textAlign: "right" }}>
                      {valores[m]?.toLocaleString("es-CL") || "-"}
                    </td>
                  ))}
                  <td style={{ padding: "0.5rem", border: "1px solid #ccc", fontWeight: "bold", textAlign: "right" }}>
                    {valores.total.toLocaleString("es-CL")}
                  </td>
                </tr>
              ))}

              <tr style={{ background: "#dee2e6", fontWeight: "bold" }}>
                <td style={{ padding: "0.5rem", border: "1px solid #ccc" }}>TOTAL</td>
                {meses.map(m => {
                  const sumaMes = Object.values(datos).reduce((acc, v) => acc + (v[m] || 0), 0);
                  return (
                    <td key={m} style={{ padding: "0.5rem", border: "1px solid #ccc", textAlign: "right" }}>
                      {sumaMes.toLocaleString("es-CL")}
                    </td>
                  );
                })}
                <td style={{ padding: "0.5rem", border: "1px solid #ccc", textAlign: "right" }}>
                  {Object.values(datos).reduce((acc, v) => acc + v.total, 0).toLocaleString("es-CL")}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
