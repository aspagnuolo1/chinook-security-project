package ch.supsi.chinook.config;

import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.service.EmployeeService;
import ch.supsi.chinook.service.PasswordEncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
public class PasswordChangeRunner implements CommandLineRunner {

    @Autowired
    private EmployeeService userService;

    @Autowired
    private PasswordEncoderService passwordEncoderService;

    @Override
    public void run(String... args) throws Exception {
        String pwd = "Jo5hu4!";
        String encPwd = passwordEncoderService.encodePassword(pwd);
        // Esegui la logica per cambiare le password
        Collection<Employee> employees = userService.findAll();
        employees.forEach(employee -> userService.updatePassword(employee.getEmail(), encPwd));
    }
}