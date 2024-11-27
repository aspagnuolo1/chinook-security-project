package ch.supsi.chinook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private Integer customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String country;

    // Metodo per ottenere il nome completo, se necessario in display
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
