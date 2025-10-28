package ch.bzz.controller;

import ch.bzz.generated.api.ProjectApi;
import ch.bzz.generated.model.LoginProject200Response;
import ch.bzz.generated.model.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectApiController implements ProjectApi {

    @Override
    public ResponseEntity<Void> createProject(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public ResponseEntity<LoginProject200Response> loginProject(LoginRequest loginRequest) {
        return null;
    }
}