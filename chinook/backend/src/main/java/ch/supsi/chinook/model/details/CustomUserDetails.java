package ch.supsi.chinook.model.details;

import ch.supsi.chinook.model.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public abstract class CustomUserDetails implements UserDetails {

    private final Employee employee;

    public CustomUserDetails(Employee employee) {
        this.employee = employee;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ritorna i ruoli dell'utente; ad esempio "ROLE_EMPLOYEE" o "ROLE_MANAGER"
        return List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()));
    }

    public Integer getId(){
        return employee.getEmployeeId();
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    public String getFullName(){
        return  employee.getFirstName() + " "+ employee.getLastName();
    }

    public String getRole(){
        return employee.getRole().name();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}