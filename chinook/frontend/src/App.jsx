// src/App.jsx
import React, { useState, useEffect } from 'react';
import {BrowserRouter as Router, Route, Routes, Link, Navigate, redirect, useNavigate} from 'react-router-dom';
import CustomerList from './components/CustomerList';
import Login from './components/Login';
import ChangePassword from './components/ChangePassword';
import './App.css';
import axios from "axios";

const App = () => {
    const [token, setToken] = useState(localStorage.getItem('token') || '');
    const [username, setUsername] = useState('');
    const [isRefreshing, setIsRefreshing] = useState(false);

    useEffect(() => {
        setToken(sessionStorage.getItem('authToken'));
        //setToken(localStorage.getItem('authToken'));
        if (token) {
            console.log("token found, redirecting to the dashboard...")
            redirect("/dashboard")
        } else{
            console.log("token not found, redirecting to the login...")
            redirect("/")
        }
    });

    const handleLogin = (newToken) => {
        console.log("Token received from Login:", newToken);
        startRefreshTimer(); // Avvia il timer per il refresh
        setToken(newToken);
        axios.get('http://localhost:8080/api/user-profile', {
            headers: {
                'Authorization': `Bearer ${newToken}`
            }
        })
            .then(response => {
                console.log("User profile data:", response.data);
                setUsername(response.data.username);
            })
            .catch(error => {
                console.error("Error fetching user profile:", error);
            });
        redirect("/dashboard")
    };

    const handleLogout = () => {
        console.log("Logging out");
        axios.get('http://localhost:8080/api/logout', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {

        });
        setToken('');
        sessionStorage.removeItem('authToken');
        //localStorage.removeItem('authToken');
        redirect("/");
    };

    let refreshTimeout;

    const startRefreshTimer = () => {
        // Imposta il timer per il refresh del token (5 minuti)
        refreshTimeout = setTimeout(async () => {
            await refreshToken();
        }, 5 * 60 * 1000); // 5 minuti
    };

    const refreshToken = async () => {
        if (isRefreshing) return; // Evita richieste multiple

        setIsRefreshing(true); // Indica che il refresh è in corso

        try {
            const response = await axios.post('http://localhost:8080/api/refresh', null, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.status === 200) {
                const newToken = response.data;
                setToken(newToken);
                sessionStorage.setItem('authToken', newToken);
                // localStorage.setItem('authToken', newToken); // Aggiorna il token nel localStorage
                startRefreshTimer(); // Riavvia il timer
            } else if (response.status === 403) {
                // Logout se non è consentito il refresh
                handleLogout();
            }
        } catch (error) {
            console.error("Error refreshing token:", error);
            handleLogout(); // Logout in caso di errore
        } finally {
            setIsRefreshing(false); // Indica che il refresh è completato
        }
    };

    useEffect(() => {
        let timeoutId;

        const handleUserActivity = () => {
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
            timeoutId = setTimeout(() => {
                console.log("Inactivity for more than 2 minutes, logging out...");
                handleLogout();
            }, 2 * 60 * 1000); // 2 minuti
        };

        window.addEventListener('mousemove', handleUserActivity);
        window.addEventListener('keydown', handleUserActivity);
        window.addEventListener('scroll', handleUserActivity);
        window.addEventListener('click', handleUserActivity);

        return () => {
            clearTimeout(timeoutId);
            window.removeEventListener('mousemove', handleUserActivity);
            window.removeEventListener('keydown', handleUserActivity);
            window.removeEventListener('scroll', handleUserActivity);
            window.removeEventListener('click', handleUserActivity);
        };
    }, []);

    return (
        <Router>
            <div className="App">
                <h1>Customer Dashboard</h1>

                {token ? (
                    <>
                        <Link to="/dashboard">
                            <button>Dashboard</button>
                        </Link>
                        <button onClick={handleLogout}>Logout</button>
                        <Link to="/change-password">
                            <button>Profile</button>
                        </Link>
                    </>
                ) : (
                    <Navigate to="/" />
                )}

                <Routes>
                    <Route
                        path="/"
                        element={token ? (
                            <Navigate to="/dashboard" />
                        ) : (
                            <Login onLogin={handleLogin} />
                        )}
                    />
                    <Route
                        path="/dashboard"
                        element={token ? (
                            <section className="customer-section">
                                <h1>Hello {username}</h1>
                                <h2>Customers list</h2>
                                <CustomerList token={token} />
                            </section>
                        ) : (
                            <div>
                                Error: you don't have the permission to visit this page
                                <Login onLogin={handleLogin} />
                                <Navigate to="/"/>
                            </div>
                        )}
                    />
                    <Route
                        path="/change-password"
                        element={token ? (
                            <ChangePassword token={token} handleLogout={handleLogout} />
                        ) : (
                            <Navigate to="/" />
                        )}
                    />
                </Routes>
            </div>
        </Router>
    );
};

export default App;
