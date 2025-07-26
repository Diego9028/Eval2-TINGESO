import api from "../http-common";

// Asegúrate de que el `http-common.js` esté apuntando al dominio/baseURL del microservicio `cliente-service`.

const crear = (cliente) => api.post("/api/clientes", cliente);

const buscarPorRut = async (rut) => {
  try {
    const res = await api.get(`/api/clientes/rut/${rut}`);
    return res.data;
  } catch {
    return null;
  }
};

const buscarPorEmail = async (email) => {
  try {
    const res = await api.get(`/api/clientes/email`, { params: { email } });
    return res.data;
  } catch {
    return null;
  }
};

export default {
  crear,
  buscarPorRut,
  buscarPorEmail,
};
