package ch.bzz.controller;

import ch.bzz.generated.api.BookingApi;
import ch.bzz.generated.model.*;
import ch.bzz.model.Project;
import ch.bzz.repository.BookingRepository;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
public class BookingApiController implements BookingApi {

    private final ProjectRepository projectRepository;
    private final BookingRepository bookingRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public BookingApiController(ProjectRepository projectRepository, JwtUtil jwtUtil, BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        this.projectRepository = projectRepository;
        this.jwtUtil = jwtUtil;
    }




    @Override
    public ResponseEntity<List<ch.bzz.generated.model.Booking>> getBookings() {
        String token = jwtUtil.verifyTokenAndExtractSubject();
        Project project = projectRepository.getReferenceById(token);
        List<ch.bzz.model.Booking> entities = bookingRepository.findByProject(project);
        List<Booking> result = (entities == null || entities.isEmpty())
                ? Collections.emptyList()
                : entities.stream()
                .map(e -> new Booking())
                .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Void> updateBookings(UpdateBookingsRequest updateBookingsRequest) {
        return null;
    }
}