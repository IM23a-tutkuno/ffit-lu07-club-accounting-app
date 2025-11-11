package ch.bzz.controller;

import ch.bzz.generated.api.AccountApi;
import ch.bzz.generated.model.*;
import ch.bzz.model.Project;
import ch.bzz.repository.AccountRepository;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountApiController implements AccountApi {

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;
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
                .map(e -> {
                    Account dto = new Account();
                    try {
                        dto.setNumber(Integer.parseInt(e.getAccountNumber()));
                    } catch (NumberFormatException ex) {
                        return null; // nicht-numerische Nummer überspringen
                    }
                    dto.setName(e.getName());
                    return dto;
                })
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Transactional
    @Override
    public ResponseEntity<Void> updateAccounts(UpdateAccountsRequest updateAccountsRequest) {
        String projectName = jwtUtil.verifyTokenAndExtractSubject();
        Project project = projectRepository.getReferenceById(projectName);

        List<ch.bzz.model.Account> existing = accountRepository.findByProject(project);
        Map<String, ch.bzz.model.Account> byNumber = existing == null ? new HashMap<>() :
                existing.stream().collect(Collectors.toMap(ch.bzz.model.Account::getAccountNumber, a -> a, (a, b) -> a));

        if (updateAccountsRequest != null && updateAccountsRequest.getAccounts() != null) {
            for (AccountUpdate incoming : updateAccountsRequest.getAccounts()) {
                if (incoming == null || incoming.getNumber() == null) continue;
                String accNo = String.valueOf(incoming.getNumber());
                JsonNullable<String> name = incoming.getName();

                if (name != null && name.isPresent()) {
                    ch.bzz.model.Account entity = byNumber.get(accNo);
                    if (entity == null) {
                        entity = new ch.bzz.model.Account();
                        entity.setProject(project);
                        entity.setAccountNumber(accNo);
                    }
                    entity.setName(name.get());
                    accountRepository.save(entity);
                    byNumber.put(accNo, entity);
                } else { // löschen
                    ch.bzz.model.Account entity = byNumber.get(accNo);
                    if (entity != null) {
                        accountRepository.delete(entity);
                        byNumber.remove(accNo);
                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }
}
