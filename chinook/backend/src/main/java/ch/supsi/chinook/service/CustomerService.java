package ch.supsi.chinook.service;

import ch.supsi.chinook.model.Customer;
import ch.supsi.chinook.model.Employee;
import ch.supsi.chinook.repository.CustomerRepository;
import ch.supsi.chinook.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAllByEmployeeId(Integer supportRepId) {
        return customerRepository.findAllBySupportRepId(supportRepId);
    }

    public List<Customer> findAll(){
        return customerRepository.findAll();
    }
}
