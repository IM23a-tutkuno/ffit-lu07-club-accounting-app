package ch.bzz.controller;

import ch.bzz.generated.api.AccountApi;
import ch.bzz.generated.api.BookingApi;
import ch.bzz.generated.api.ProjectApi;
import ch.bzz.generated.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

public class BookingApiController implements BookingApi {

    @Override
    public ResponseEntity<List<Booking>> getBookings() {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateBookings(UpdateBookingsRequest updateBookingsRequest) {
        return null;
    }
}