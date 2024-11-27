package ch.supsi.chinook.model.details;

import ch.supsi.chinook.model.Employee;

public class EmployeeDetails extends CustomUserDetails{
    public EmployeeDetails(Employee employee) {
        super(employee);
    }
}
