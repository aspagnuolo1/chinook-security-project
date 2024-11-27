package ch.supsi.chinook.model.dto;


import jakarta.annotation.Nonnull;
import lombok.NonNull;

public class AuthRequest {
    private String email;

    private String password;

    // Costruttore
    public AuthRequest() {}

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter e Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}