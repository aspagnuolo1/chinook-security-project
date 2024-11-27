package ch.supsi.chinook.service;

import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.model.PasswordChange;
import ch.supsi.chinook.model.details.CustomUserDetails;
import ch.supsi.chinook.model.dto.AuthRequest;
import ch.supsi.chinook.model.dto.AuthResponse;
import ch.supsi.chinook.repository.EmployeeRepository;
import ch.supsi.chinook.repository.PasswordChangeRepository;
import ch.supsi.chinook.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoderService passwordEncoderService;
    private final PasswordChangeRepository passwordChangeRepository;
    private final EmployeeService employeeService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, PasswordEncoderService passwordEncoderService, PasswordChangeRepository passwordChangeRepository, EmployeeService employeeService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoderService = passwordEncoderService;
        this.passwordChangeRepository = passwordChangeRepository;
        this.employeeService = employeeService;
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            String token = jwtUtil.generateToken(auth);
            return new AuthResponse(token, "Login successful" ,"/dashboard");
        } catch (AuthenticationException e) {
            System.out.println("3 invalid credentials");
            return new AuthResponse(null, "Invalid credentials", "/");
        }
    }

    public void logout(String authHeader) {
        String token = authHeader.substring(7);
        jwtUtil.expireToken(token);
    }

    public boolean checkPassword(String username, String rawPassword) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
        return passwordEncoderService.matches(rawPassword, userDetails.getPassword());
    }

    public String extractUsername(String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }

    public boolean checkOldPasswords(String username, String rawPassword) {
        Employee employee = employeeService.findByEmail(username);

        // Recupera tutte le vecchie password codificate dell'utente
        List<String> oldPasswords = passwordChangeRepository.findByEmployee(employee)
                .stream()
                .map(PasswordChange::getOldPassword)
                .toList();

        // Verifica se la password in chiaro corrisponde a una delle vecchie
        return oldPasswords.stream()
                .noneMatch(encodedPassword -> passwordEncoderService.matches(rawPassword, encodedPassword));
    }
}