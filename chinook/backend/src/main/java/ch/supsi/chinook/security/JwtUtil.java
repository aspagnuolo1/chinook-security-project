package ch.supsi.chinook.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private long expirationTime = 5*60*1000;

    public String generateToken(Authentication authentication) {
        // Estrai i ruoli dall'oggetto Authentication
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role) // Aggiungi il prefisso se necessario
                .collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", roles)  // Aggiungi i ruoli come claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey) // Assicurati di usare il giusto algoritmo
                .compact();
    }

    public String extractUsername(String token) {
        String username = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return username;
    }

    public boolean validateToken(String token, String username) {

        String extractedUsername = extractUsername(token);
        boolean rolesMatch = false;

        // Estrai i ruoli dal token per il controllo
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        List<String> roles = claims.get("roles", List.class);

        // Verifica che il nome utente corrisponda e che il token non sia scaduto
        return (extractedUsername.equals(username) && !isTokenExpired(token) && roles != null);
    }


    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public String expireToken(String token){
        if(validateToken(token, extractUsername(token))){
            // Ottieni i claim dal token esistente
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Imposta la nuova data di scadenza (ora)
            claims.setExpiration(new Date(System.currentTimeMillis())); // Scadenza immediata

            // Rigenera il token con i nuovi claim (scadenza aggiornata)
            String newToken = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(claims.getSubject())
                    .setIssuedAt(new Date())
                    .setExpiration(claims.getExpiration()) // Nuova scadenza
                    .signWith(secretKey)
                    .compact();

            // Ora il token è scaduto, non lo restituire ma invalida il token.
            return null;
        }

        return null;
    }

}