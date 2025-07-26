import { useState, useEffect } from "react";
import dayjs from "dayjs";
import {
  Box, TextField, Button, MenuItem, Typography, IconButton
} from "@mui/material";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";

import tarifaService from "../services/tarifa.service";
import reservaService from "../services/reserva.service";

export default function ReservasNueva() {
  const [tarifas, setTarifas] = useState([]);
  const [reservaId, setReservaId] = useState(null);

  const [form, setForm] = useState({
    correoTitular: "",
    fechaHoraReserva: dayjs(),
    tarifaId: "",
  });

  const [correosParticipantes, setCorreosParticipantes] = useState([""]);

  useEffect(() => {
    tarifaService.getAll().then(r => setTarifas(r.data));
  }, []);

  const handleChangeForm = e =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleCorreoParticipanteChange = (index, value) => {
    const actualizados = [...correosParticipantes];
    actualizados[index] = value;
    setCorreosParticipantes(actualizados);
  };

  const agregarParticipante = () => {
    setCorreosParticipantes([...correosParticipantes, ""]);
  };

  const eliminarParticipante = (index) => {
    const actualizados = correosParticipantes.filter((_, i) => i !== index);
    setCorreosParticipantes(actualizados);
  };

  const handleSubmit = async e => {
    e.preventDefault();

    try {
      const tarifa = tarifas.find(t => t.id == form.tarifaId);
      if (!tarifa) throw new Error("Tarifa inválida");

      const payload = {
        correoTitular: form.correoTitular,
        correosParticipantes,
        numeroVueltas: tarifa.numeroVueltas,
        fechaHoraReserva: form.fechaHoraReserva.format('YYYY-MM-DDTHH:mm:ss')
      };

      const r = await reservaService.create(payload);
      setReservaId(r.data.id);
      alert("Reserva creada exitosamente. El comprobante fue enviado por correo.");
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Error al crear reserva");
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ p: 3, maxWidth: 500 }}>
      <Typography variant="h5" sx={{ mb: 2 }}>Nueva reserva</Typography>

      {/* Titular */}
      <TextField
        label="Correo del titular"
        name="correoTitular"
        type="email"
        required
        fullWidth
        value={form.correoTitular}
        onChange={handleChangeForm}
        sx={{ mb: 2 }}
      />

      {/* Participantes */}
      <Typography variant="subtitle1">Correos de participantes</Typography>
      {correosParticipantes.map((correo, index) => (
        <Box key={index} sx={{ display: "flex", alignItems: "center", mb: 1 }}>
          <TextField
            type="email"
            label={`Participante ${index + 1}`}
            value={correo}
            required
            fullWidth
            onChange={(e) => handleCorreoParticipanteChange(index, e.target.value)}
          />
          <IconButton onClick={() => eliminarParticipante(index)} disabled={correosParticipantes.length === 1}>
            <RemoveIcon />
          </IconButton>
        </Box>
      ))}

      <Button
        variant="outlined"
        startIcon={<AddIcon />}
        onClick={agregarParticipante}
        sx={{ mb: 2 }}
      >
        Agregar participante
      </Button>

      {/* Tarifa */}
      <TextField
        select
        fullWidth
        required
        name="tarifaId"
        label="Tarifa"
        value={form.tarifaId}
        onChange={handleChangeForm}
        sx={{ mb: 2 }}
      >
        {tarifas.map(t => (
          <MenuItem key={t.id} value={t.id}>
            {t.numeroVueltas} vueltas – ${t.precio}
          </MenuItem>
        ))}
      </TextField>

      {/* Fecha */}
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DateTimePicker
          label="Fecha y hora"
          value={form.fechaHoraReserva}
          onChange={fecha => setForm({ ...form, fechaHoraReserva: fecha })}
          sx={{ mb: 2, width: "100%" }}
        />
      </LocalizationProvider>

      {/* Enviar */}
      <Button variant="contained" type="submit" fullWidth>
        Reservar
      </Button>

      {reservaId && (
        <Typography sx={{ mt: 2 }} color="success.main">
          Reserva #{reservaId} creada exitosamente.
        </Typography>
      )}
    </Box>
  );
}
