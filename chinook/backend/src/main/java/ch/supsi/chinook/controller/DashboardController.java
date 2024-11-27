package ch.supsi.chinook.controller;

import ch.supsi.chinook.model.Customer;
import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.model.Log;
import ch.supsi.chinook.model.details.CustomUserDetails;
import ch.supsi.chinook.model.dto.CustomerDTO;
import ch.supsi.chinook.model.dto.DashboardResponse;
import ch.supsi.chinook.security.JwtUtil;
import ch.supsi.chinook.service.CustomUserDetailsService;
import ch.supsi.chinook.service.CustomerService;
import ch.supsi.chinook.service.EmployeeService;
import ch.supsi.chinook.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private JwtUtil jwtUtil; // Per decodificare il token
    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Servizio per recuperare i dati dell'utente
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private EmployeeService employeeService;


    @GetMapping(value = "/dashboard")
    public ResponseEntity<DashboardResponse> getDashboardDetails(@RequestHeader("Authorization") String authorizationHeader) {
        // Rimuovi il prefisso "Bearer " dall'header
        String token = authorizationHeader.substring(7);
        // Verifica e decodifica il token
        String username = jwtUtil.extractUsername(token);

        Log log = new Log();
        log.setDate(new Date());

        if (username == null || !jwtUtil.validateToken(token, username)) {
            log.setEmployee(null);
            log.setUsername(null);
            log.setMessage("Dashboard access not permitted");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Recupera i dettagli dell'utente
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
        log.setEmployee(employeeService.findByEmail(username));
        log.setUsername(username);
        List<Customer> customers;
        // Verifica il ruolo dell'utente e recupera i customer di conseguenza
        if ("MANAGER".equals(userDetails.getRole())) {
            // Se è un manager, ottieni tutti i customer
            customers = customerService.findAll();
        } else if ("EMPLOYEE".equals(userDetails.getRole())) {
            // Se è un employee, ottieni solo i customer legati al suo ID
            customers = customerService.findAllByEmployeeId(userDetails.getId());
        } else {
            // Se il ruolo non è riconosciuto, restituisci UNAUTHORIZED
            log.setMessage("Dashboard access not permitted");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Mappa i customer in CustomerDTO
        List<CustomerDTO> dtoCustomers = customers.stream()
                .map(this::toCustomerDTO)
                .toList();

        // Crea l'oggetto di risposta per la dashboard
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setCustomers(dtoCustomers);
        log.setMessage("Dashboard retrieved successfully");
        return new ResponseEntity<>(dashboardResponse, HttpStatus.OK);
    }


    private CustomerDTO toCustomerDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCountry()
        );
    }
}