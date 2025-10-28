package ch.bzz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @Column(name = "projectName", nullable = false, unique = true, length = 100)
    private String projectName;

    @Column(name = "passwordHash", nullable = false, length = 255)
    private String passwordHash;
}
