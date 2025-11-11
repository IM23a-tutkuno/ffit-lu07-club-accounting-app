package ch.bzz.controller;

import ch.bzz.generated.api.AccountApi;
import ch.bzz.generated.api.ProjectApi;
import ch.bzz.generated.model.*;
import ch.bzz.model.Project;
import ch.bzz.repository.AccountRepository;
import ch.bzz.repository.BookingRepository;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class AccountApiController implements AccountApi {

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AccountApiController(ProjectRepository projectRepository, JwtUtil jwtUtil, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.projectRepository = projectRepository;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public ResponseEntity<List<ch.bzz.generated.model.Account>> getAccounts() {
        String token = jwtUtil.verifyTokenAndExtractSubject();
        Project project = projectRepository.getReferenceById(token);
        List<ch.bzz.model.Account> entities = accountRepository.findByProject(project);
        List<Account> result = (entities == null || entities.isEmpty())
                ? Collections.emptyList()
                : entities.stream()
                .map(e -> new Account())
                .toList();
        return ResponseEntity.ok(result);
    }


    @Override
    public ResponseEntity<Void> updateAccounts(UpdateAccountsRequest updateAccountsRequest) {
        return null;
    }
}
