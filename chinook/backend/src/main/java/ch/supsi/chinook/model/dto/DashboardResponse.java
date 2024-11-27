package ch.supsi.chinook.model.dto;

import ch.supsi.chinook.model.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DashboardResponse {
    private List<CustomerDTO> customers;

}
