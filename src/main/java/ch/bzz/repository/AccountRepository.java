package ch.bzz.repository;

import ch.bzz.model.Account;
import ch.bzz.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByProject(Project project);
}
