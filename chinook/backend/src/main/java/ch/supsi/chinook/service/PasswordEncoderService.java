package ch.supsi.chinook.service;

import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordEncoderService {

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public PasswordEncoderService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    private void encodePasswords(String defaultPassword) {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            // Imposta una password di default se non è già presente
            if (employee.getPassword() == null) {
                String encodedPassword = passwordEncoder.encode(defaultPassword);
                employee.setPassword(encodedPassword);
                employeeRepository.save(employee); // Salva l'employee con la password codificata
            }
        }
    }
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}