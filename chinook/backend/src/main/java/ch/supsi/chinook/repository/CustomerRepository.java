package ch.supsi.chinook.repository;

import ch.supsi.chinook.model.Customer;
import ch.supsi.chinook.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findAllBySupportRepId(Integer supportRepId);
}
