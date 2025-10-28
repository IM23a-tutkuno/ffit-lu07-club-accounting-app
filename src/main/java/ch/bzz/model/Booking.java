package ch.bzz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "text", nullable = false, length = 255)
    private String text;

    @Column(name = "amount", nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(name = "debitAccountId", nullable = false)
    private Account debitAccount;

    @ManyToOne
    @JoinColumn(name = "creditAccountId", nullable = false)
    private Account creditAccount;

    @ManyToOne
    @JoinColumn(name = "projectName", nullable = false)
    private Project project;
}
