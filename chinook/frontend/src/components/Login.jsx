import React, { useState, useEffect } from 'react';
import {redirect, useNavigate} from 'react-router-dom';
import axios from 'axios';

const Login = ({ onLogin }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);

    // Controlla se l'utente è già loggato al caricamento del componente
    useEffect(() => {
        const token = localStorage.getItem('authToken');
        if (token) {
            console.log("token found, redirecting to the dashboard...")
            redirect("/dashboard")
        } else{
            console.log("Please, login")
        }
    });

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(
                'http://localhost:8080/api/login',
                { email, password },
                {
                    headers: { 'Content-Type': 'application/json' }
                }
            );

            // Memorizza il token nel localStorage e chiama la funzione onLogin
            sessionStorage.setItem('authToken', response.data.token);

            //localStorage.setItem('authToken', response.data.token);
            onLogin(response.data.token); // Passa il token al componente principale
            setError(null);

            // Dopo aver fatto login, reindirizza alla dashboard
        } catch (err) {
            sessionStorage.removeItem('authToken');
            //localStorage.removeItem('authToken');
            setError('Login failed. Please check your credentials.');
        }
    };

    return (
        <div>
            <h2>Login</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <form onSubmit={handleLogin}>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default Login;
