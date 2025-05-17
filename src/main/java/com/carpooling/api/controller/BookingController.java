package com.carpooling.api.controller;

import com.carpooling.api.dto.BookingDTO;
import com.carpooling.api.enums.BookingStatus;
import com.carpooling.api.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/{rideId}/passenger/{passengerId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<BookingDTO> createBooking(
            @PathVariable Long rideId,
            @PathVariable Long passengerId,
            @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.createBooking(rideId, passengerId, bookingDTO));
    }

    @GetMapping("/passenger/{passengerId}")
    @PreAuthorize("hasRole('PASSENGER') and @userSecurity.isCurrentUser(#passengerId)")
    public ResponseEntity<List<BookingDTO>> getPassengerBookings(@PathVariable Long passengerId) {
        return ResponseEntity.ok(bookingService.getPassengerBookings(passengerId));
    }

    @GetMapping("/ride/{rideId}")
    @PreAuthorize("hasRole('DRIVER') and @userSecurity.isRideDriver(#rideId)")
    public ResponseEntity<List<BookingDTO>> getRideBookings(@PathVariable Long rideId) {
        return ResponseEntity.ok(bookingService.getRideBookings(rideId));
    }

    @PutMapping("/{bookingId}/status")
    @PreAuthorize("hasRole('DRIVER') and @userSecurity.isBookingRideDriver(#bookingId)")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam String status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(bookingId, BookingStatus.valueOf(status)));
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('DRIVER')")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('PASSENGER') and @userSecurity.isBookingPassenger(#bookingId)")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok().build();
    }
} 