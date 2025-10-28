package ch.bzz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private String accountNumber;
    private String name;
    private Project project;  // Beziehung zu Project
}
