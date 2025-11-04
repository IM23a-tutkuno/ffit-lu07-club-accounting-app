package ch.bzz.controller;

import ch.bzz.generated.api.ProjectApi;
import lombok.extern.slf4j.Slf4j;
import ch.bzz.generated.model.LoginProject200Response;
import ch.bzz.generated.model.LoginRequest;
import ch.bzz.model.Project;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProjectApiController implements ProjectApi {

    private final ProjectRepository projectRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public ProjectApiController(ProjectRepository projectRepository, JwtUtil jwtUtil) {
        this.projectRepository = projectRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<Void> createProject(LoginRequest loginRequest) {
        String projectName = loginRequest.getProjectName();
        String password = loginRequest.getPassword();
        log.info("Project Name: " + projectName);

        // ProjectRepository: Standardmethode existsById(...)
        if (projectRepository.existsById(projectName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Project project = new Project();

        // HIER an deine Entity anpassen:
        // Wenn projectName die ID ist:
        project.setProjectName(projectName);
        project.setPasswordHash(encoder.encode(password));

        projectRepository.save(project);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Override
    public ResponseEntity<LoginProject200Response> loginProject(LoginRequest loginRequest) {
        String projectName = loginRequest.getProjectName();
        String password = loginRequest.getPassword();

        // ProjectRepository: Standardmethode findById(...)
        return projectRepository.findById(projectName)
                .map(project -> {
                    boolean ok = encoder.matches(password, project.getPasswordHash());
                    if (!ok) {
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .<LoginProject200Response>build();
                    }

                    String token = jwtUtil.generateToken(projectName);

                    LoginProject200Response response = new LoginProject200Response().accessToken(token);

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                );
    }
}