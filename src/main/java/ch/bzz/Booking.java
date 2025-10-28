package ch.bzz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDate date;
    private String text;
    private Account debitAccount;   // Soll-Konto
    private Account creditAccount;  // Haben-Konto
    private double amount;
    private Project project;        // Bezug zu Project
}
