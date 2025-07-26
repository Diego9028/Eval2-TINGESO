// src/services/tarifa.service.js
import api from '../http-common';

const getAll = () => api.get('/api/tarifas');
const getByVueltas = (vueltas) => api.get(`/api/tarifas/by-vueltas?numeroVueltas=${vueltas}`);

export default { getAll, getByVueltas };
