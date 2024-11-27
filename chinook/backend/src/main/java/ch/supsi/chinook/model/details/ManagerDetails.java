package ch.supsi.chinook.model.details;

import ch.supsi.chinook.model.Employee;

public class ManagerDetails extends CustomUserDetails{
    public ManagerDetails(Employee employee) {
        super(employee);
    }
}
