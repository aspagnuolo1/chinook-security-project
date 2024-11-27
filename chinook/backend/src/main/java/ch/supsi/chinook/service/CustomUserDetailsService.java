package ch.supsi.chinook.service;

import ch.supsi.chinook.model.Role;
import ch.supsi.chinook.model.details.CustomUserDetails;
import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.model.details.EmployeeDetails;
import ch.supsi.chinook.model.details.ManagerDetails;
import ch.supsi.chinook.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Employee> user = employeeRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Employee employee = user.get();
        // Controlla il ruolo dell'utente
        Role role = employee.getRole(); // Assumendo che tu abbia un metodo per ottenere il ruolo
        if (Role.MANAGER.equals(role)) {
            // Restituisci dati specifici per il manager
            return new ManagerDetails(employee);
        } else {
            // Restituisci dati per un employee normale
            return new EmployeeDetails(employee);
        }
    }
}
