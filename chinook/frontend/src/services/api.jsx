// src/services/api.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/api'; // URL del backend

// Funzione per ottenere tutti i clienti
export const getCustomers = async () => {
    try {
        const response = await axios.get(`${API_URL}/dashboard`);
        return response.data;
    } catch (error) {
        console.error("Error fetching customers:", error);
        throw error;
    }
};
