package ch.bzz.controller;

import ch.bzz.generated.api.AccountApi;
import ch.bzz.generated.api.ProjectApi;
import ch.bzz.generated.model.Account;
import ch.bzz.generated.model.LoginProject200Response;
import ch.bzz.generated.model.LoginRequest;
import ch.bzz.generated.model.UpdateAccountsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountApiController implements AccountApi {

    @Override
    public ResponseEntity<List<Account>> getAccounts() {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateAccounts(UpdateAccountsRequest updateAccountsRequest) {
        return null;
    }
}
