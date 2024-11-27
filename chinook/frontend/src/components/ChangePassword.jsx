// src/components/ChangePassword.jsx
import React, { useState } from 'react';
import {redirect, useNavigate} from 'react-router-dom';
import axios from 'axios';

const ChangePassword = ({ token, handleLogout }) => {  // Aggiungi handleLogout come prop
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post("http://localhost:8080/api/change-pw", {
                oldPassword: oldPassword,
                newPassword: newPassword
            }, {
                headers: {
                    Authorization: `Bearer ${token}`  // Usa il token passato come prop
                }
            });

            if (response.status === 200) {
                // Successo: rimuovi il token e invoca handleLogout
                sessionStorage.removeItem("authToken")
                handleLogout();  // Chiamato per fare il logout e reindirizzare l'utente
                redirect("/");  // Reindirizza alla pagina di login (opzionale)
            }

            console.log("Password changed successfully:", response.data);
        } catch (error) {
            console.error("Errore nel cambiare la password:", error.response?.data || error.message);
            setMessage("Errore nel cambiare la password. Riprova.");
        }
    };

    return (
        <div className="change-password">
            <h2>Change Password</h2>
            {message && <p>{message}</p>}
            <form onSubmit={handleSubmit}>
                <label>
                    Current Password:
                    <input
                        type="password"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        required
                    />
                </label>
                <br />
                <label>
                    New password:
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                    />
                </label>
                <br />
                <label>
                    Confirm new password:
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </label>
                <br />
                <button type="submit">Change Password</button>
            </form>
        </div>
    );
};

export default ChangePassword;
