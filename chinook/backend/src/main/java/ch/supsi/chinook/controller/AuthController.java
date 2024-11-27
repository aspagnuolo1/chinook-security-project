package ch.supsi.chinook.controller;

import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.model.Log;
import ch.supsi.chinook.model.details.CustomUserDetails;
import ch.supsi.chinook.model.dto.AuthRequest;
import ch.supsi.chinook.model.dto.AuthResponse;
import ch.supsi.chinook.security.JwtUtil;
import ch.supsi.chinook.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final PasswordEncoderService passwordEncoderService;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmployeeService employeeService;
    private final LoggingService loggingService;

    public AuthController(JwtUtil jwtUtil, AuthService authService, PasswordEncoderService passwordEncoderService, CustomUserDetailsService customUserDetailsService, EmployeeService employeeService, LoggingService loggingService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.passwordEncoderService = passwordEncoderService;
        this.customUserDetailsService = customUserDetailsService;
        this.employeeService = employeeService;
        this.loggingService = loggingService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticate(authRequest);
        Log log = new Log();
        log.setDate(new Date());
        if(authResponse.getToken()==null){
            log.setEmployee(employeeService.findByEmail(authRequest.getEmail()));
            log.setUsername(authRequest.getEmail());
            log.setMessage("Login failed");
            loggingService.logActivity(log);
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);

        } else{
            log.setEmployee(employeeService.findByEmail(authRequest.getEmail()));
            log.setUsername(authRequest.getEmail());
            log.setMessage("Login successful");
            loggingService.logActivity(log);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);

        }
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
        Employee employee = employeeService.findByEmail(authService.extractUsername(authorizationHeader));
        Log log = new Log();
        log.setDate(new Date());
        log.setEmployee(employee);
        log.setUsername(employee.getEmail());
        log.setMessage("Logout successful");
        loggingService.logActivity(log);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String us = authentication.getName();
        Employee employee = employeeService.findByEmail(us);
        Log log = new Log();
        log.setDate(new Date());
        log.setEmployee(employee);
        log.setUsername(employee.getEmail());


        // Controlla se l'utente ha il ruolo di manager
        boolean isManager = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"));

        if (isManager) {
            // Genera un nuovo token per l'utente manager
            String newToken = jwtUtil.generateToken(authentication);
            log.setMessage("Refresh for manager successful");
            return ResponseEntity.ok(newToken);
        } else {
            // Se l'utente non è un manager, ritorna 403 Forbidden
            log.setMessage("Refresh not for manager unsuccessful");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/user-profile")
    public Map<String, String> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            System.err.println("Authentication is null");
        }
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(authentication.getName());
        String username = authentication.getName();
        Employee employee = employeeService.findByEmail(username);
        Log log = new Log();
        log.setDate(new Date());
        log.setEmployee(employee);
        log.setUsername(employee.getEmail());
        log.setMessage("User profile retrieved successfully");
        return Map.of("username", userDetails.getFullName());
    }

    @PostMapping("/change-pw")
    public ResponseEntity<String> changePassword(
            @RequestBody Map<String, String> passwordData,
            Authentication authentication) {

        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        // Recupera i dettagli dell'utente corrente
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(authentication.getName());
        String username = authentication.getName();
        Employee employee = employeeService.findByEmail(username);
        Log log = new Log();
        log.setDate(new Date());
        log.setEmployee(employee);
        log.setUsername(employee.getEmail());

        // Verifica se la password corrente è corretta usando authService
        if (!authService.checkPassword(userDetails.getUsername(), oldPassword)) {
            log.setMessage("Pwd change unsuccessful, old password is not correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La password attuale non è corretta.");
        }

        // Verifica la complessità della nuova password
        if (!isPasswordComplexEnough(newPassword)) {
            log.setMessage("Pwd change unsuccessful, not strong enough");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La nuova password non soddisfa i requisiti di complessità.");
        }

        if(!authService.checkOldPasswords(userDetails.getUsername(), newPassword)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La nuova password non può essere uguale ad una tra le ultime 5.");
        }

        // Codifica la nuova password e aggiorna tramite employeeService
        String encodedPassword = passwordEncoderService.encodePassword(newPassword);
        employeeService.updatePassword(userDetails.getUsername(), encodedPassword);
        log.setMessage("Pwd change successful");
        loggingService.logActivity(log);
        return ResponseEntity.ok("Password aggiornata con successo. Effettua di nuovo il login.");
    }


    private boolean isPasswordComplexEnough(String password) {
        if (password.length() < 6 || password.length() > 14) {
            return false;
        }

        boolean hasUppercase = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLowercase = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[!@#$%^&*()_+=|<>?{}\\[\\]~-]").matcher(password).find();

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

}
