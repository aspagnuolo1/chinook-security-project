package ch.supsi.chinook.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Generated;

@Getter
@Setter
public class AuthResponse {

    private String token; // JWT o altro token di autenticazione
    private String message; // Messaggio di errore o successo
    private String redirecturl;

    // Costruttore
    public AuthResponse() {}

    public AuthResponse(String token, String message, String redirectUrl) {
        this.token = token;
        this.message = message;
        this.redirecturl = redirectUrl;
    }
}