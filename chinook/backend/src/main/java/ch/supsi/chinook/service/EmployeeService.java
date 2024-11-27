package ch.supsi.chinook.service;

import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.model.PasswordChange;
import ch.supsi.chinook.repository.EmployeeRepository;
import ch.supsi.chinook.repository.PasswordChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PasswordChangeRepository passwordChangeRepository;

    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }

    public Employee findByEmail(String email){
        return employeeRepository.findByEmail(email).orElse(null);
    }

    public void updatePassword(String username, String encodedPassword) {
        Employee employee = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        employee.setPassword(encodedPassword);
        employeeRepository.save(employee);

        PasswordChange passwordChange = new PasswordChange();
        passwordChange.setEmployee(employee);
        passwordChange.setOldPassword(encodedPassword);

        List<PasswordChange> passwordChanges = passwordChangeRepository.findByEmployeeOrderByCreatedAtAsc(employee);

        if(passwordChanges.size() < 5){
            passwordChangeRepository.save(passwordChange);
        } else{
            // Elimina il record più vecchio
            PasswordChange oldestPasswordChange = passwordChanges.get(0); // Primo elemento in ordine crescente
            passwordChangeRepository.delete(oldestPasswordChange);

            // Salva il nuovo record
            passwordChangeRepository.save(passwordChange);
        }
    }
}
