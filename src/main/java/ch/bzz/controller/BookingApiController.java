package ch.bzz.controller;

import ch.bzz.generated.api.BookingApi;
import ch.bzz.generated.model.*;
import ch.bzz.model.Project;
import ch.bzz.repository.AccountRepository;
import ch.bzz.repository.BookingRepository;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class BookingApiController implements BookingApi {

    private final ProjectRepository projectRepository;
    private final BookingRepository bookingRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public BookingApiController(ProjectRepository projectRepository, JwtUtil jwtUtil, BookingRepository bookingRepository, AccountRepository accountRepository) {
        this.bookingRepository = bookingRepository;
        this.projectRepository = projectRepository;
        this.accountRepository = accountRepository;
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
                .map(e -> {
                    Booking dto = new Booking();
                    dto.setId(e.getId() == null ? null : e.getId().intValue());
                    dto.setDate(e.getDate());
                    dto.setText(e.getText());
                    try {
                        dto.setDebit(e.getDebitAccount() == null ? null : Integer.parseInt(e.getDebitAccount().getAccountNumber()));
                    } catch (NumberFormatException ex) {
                        dto.setDebit(null);
                    }
                    try {
                        dto.setCredit(e.getCreditAccount() == null ? null : Integer.parseInt(e.getCreditAccount().getAccountNumber()));
                    } catch (NumberFormatException ex) {
                        dto.setCredit(null);
                    }
                    dto.setAmount((float) e.getAmount());
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(result);
    }

    @Transactional
    @Override
    public ResponseEntity<Void> updateBookings(UpdateBookingsRequest updateBookingsRequest) {
        String projectName = jwtUtil.verifyTokenAndExtractSubject();
        Project project = projectRepository.getReferenceById(projectName);

        List<ch.bzz.model.Booking> existing = bookingRepository.findByProject(project);
        Map<Long, ch.bzz.model.Booking> byId = existing == null ? new HashMap<>() :
                existing.stream().filter(b -> b.getId() != null)
                        .collect(Collectors.toMap(ch.bzz.model.Booking::getId, b -> b, (a, b) -> a));

        // Konten-Map für schnellen Zugriff
        Map<String, ch.bzz.model.Account> accountsByNumber = accountRepository.findByProject(project)
                .stream().collect(Collectors.toMap(ch.bzz.model.Account::getAccountNumber, a -> a, (a, b) -> a));

        if (updateBookingsRequest != null && updateBookingsRequest.getEntries() != null) {
            for (BookingUpdate upd : updateBookingsRequest.getEntries()) {
                if (upd == null) continue;
                Integer updId = upd.getId();

                boolean anyFieldPresent = isPresent(upd.getDate()) || isPresent(upd.getText())
                        || isPresent(upd.getDebit()) || isPresent(upd.getCredit()) || isPresent(upd.getAmount());

                if (updId == null) {
                    // Create
                    if (!anyFieldPresent) continue; // nichts zu tun
                    if (!(isPresent(upd.getDate()) && isPresent(upd.getText()) && isPresent(upd.getDebit()) && isPresent(upd.getCredit()) && isPresent(upd.getAmount()))) {
                        // unvollständig -> überspringen (minimalistisch)
                        continue;
                    }
                    ch.bzz.model.Booking entity = new ch.bzz.model.Booking();
                    entity.setProject(project);
                    entity.setDate(upd.getDate().get());
                    entity.setText(upd.getText().get());
                    ch.bzz.model.Account debit = accountsByNumber.get(String.valueOf(upd.getDebit().get()));
                    ch.bzz.model.Account credit = accountsByNumber.get(String.valueOf(upd.getCredit().get()));
                    if (debit == null || credit == null) {
                        continue; // unbekanntes Konto -> überspringen
                    }
                    entity.setDebitAccount(debit);
                    entity.setCreditAccount(credit);
                    entity.setAmount(upd.getAmount().get());
                    bookingRepository.save(entity);
                    if (entity.getId() != null) byId.put(entity.getId(), entity);
                } else {
                    // Update oder Delete
                    ch.bzz.model.Booking entity = byId.get(Long.valueOf(updId));
                    if (entity == null) continue; // Fremdprojekt oder nicht existent

                    if (!anyFieldPresent) {
                        bookingRepository.delete(entity);
                        byId.remove(Long.valueOf(updId));
                        continue;
                    }

                    if (isPresent(upd.getDate())) entity.setDate(upd.getDate().get());
                    if (isPresent(upd.getText())) entity.setText(upd.getText().get());
                    if (isPresent(upd.getAmount())) entity.setAmount(upd.getAmount().get());
                    if (isPresent(upd.getDebit())) {
                        ch.bzz.model.Account debit = accountsByNumber.get(String.valueOf(upd.getDebit().get()));
                        if (debit != null) entity.setDebitAccount(debit);
                    }
                    if (isPresent(upd.getCredit())) {
                        ch.bzz.model.Account credit = accountsByNumber.get(String.valueOf(upd.getCredit().get()));
                        if (credit != null) entity.setCreditAccount(credit);
                    }
                    bookingRepository.save(entity);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    private static <T> boolean isPresent(JsonNullable<T> v) {
        return v != null && v.isPresent();
    }
}