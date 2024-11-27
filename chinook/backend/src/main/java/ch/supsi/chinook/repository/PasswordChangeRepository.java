package ch.supsi.chinook.repository;

import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.model.PasswordChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordChangeRepository extends JpaRepository<PasswordChange, Long> {
    public List<PasswordChange> findByEmployee(Employee employee);
    List<PasswordChange> findByEmployeeOrderByCreatedAtAsc(Employee employee);

}
