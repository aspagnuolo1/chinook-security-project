// src/components/CustomerList.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import CustomerItem from './CustomerItem';

const CustomerList = ({ token }) => {
    const [customers, setCustomers] = useState([]);
    const [filteredCustomers, setFilteredCustomers] = useState([]); // Stato per i clienti filtrati
    const [searchTerm, setSearchTerm] = useState(''); // Stato per il termine di ricerca


    useEffect(() => {
        const fetchCustomers = async () => {
            try {
                //token = localStorage.getItem("authToken");
                token = sessionStorage.getItem("authToken");

                const response = await axios.get('http://localhost:8080/api/dashboard', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                console.log("Response data:", response.data); // Aggiungi questa linea per vedere la risposta
                setCustomers(response.data.customers); // Assicurati di accedere a customers
                setFilteredCustomers(response.data.customers); // Inizializza la lista filtrata
            } catch (error) {
                console.error("Error fetching customers:", error);
            }
        };


        fetchCustomers();
    }, [token]);

    const handleSearchChange = (event) => {
        const term = event.target.value.toLowerCase(); // Ottieni il termine di ricerca in minuscolo
        setSearchTerm(term);

        // Filtra i clienti in base al termine di ricerca
        const filtered = customers.filter(customer =>
            customer.firstName.toLowerCase().includes(term) || // Filtra per nome
            customer.lastName.toLowerCase().includes(term) || // Filtra per cognome
            customer.email.toLowerCase().includes(term) || // Filtra per email
            customer.country.toLowerCase().includes(term) // Filtra per paese
        );

        setFilteredCustomers(filtered); // Aggiorna la lista filtrata
    };

    return (
        <div className="customer-list">
            <input
                type="text"
                placeholder="Cerca clienti..."
                value={searchTerm}
                onChange={handleSearchChange}
                className="search-input" // Aggiungi una classe per lo stile
            />
            {filteredCustomers.length > 0 ? (
                <ul>
                    {filteredCustomers.map(customer => (
                        <CustomerItem key={customer.customerId} customer={customer} />
                    ))}
                </ul>
            ) : (
                <p>Nessun cliente trovato.</p> // Messaggio se non ci sono clienti filtrati
            )}
        </div>
    );
};

export default CustomerList;
