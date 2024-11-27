package ch.supsi.chinook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PasswordChange {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Employee employee;

    @Column
    private String oldPassword;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Imposta la data di creazione automaticamente

}
