package com.carpooling.api.service;

import com.carpooling.api.dto.BookingDTO;
import com.carpooling.api.entity.*;
import com.carpooling.api.repository.BookingRepository;
import com.carpooling.api.repository.RideRepository;
import com.carpooling.api.repository.UserRepository;
import com.carpooling.api.repository.PassengerRepository;
import com.carpooling.api.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;

    @Transactional
    public BookingDTO createBooking(Long rideId, Long passengerId, BookingDTO bookingDTO) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        if (ride.getAvailableSeats() < bookingDTO.getSeats()) {
            throw new RuntimeException("Not enough seats available");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setSeats(bookingDTO.getSeats());
        booking.setPrice(BigDecimal.valueOf(bookingDTO.getPrice().doubleValue()));
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());

        ride.setAvailableSeats(ride.getAvailableSeats() - bookingDTO.getSeats());
        rideRepository.save(ride);

        return convertToDTO(bookingRepository.save(booking));
    }

    public BookingDTO updateBookingPrice(Long bookingId, Double newPrice, String negotiationNote) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setPrice(BigDecimal.valueOf(newPrice));
                    booking.setNegotiationNote(negotiationNote);
                    booking.setUpdatedAt(LocalDateTime.now());
                    return convertToDTO(bookingRepository.save(booking));
                })
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public BookingDTO acceptBookingPrice(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setStatus(BookingStatus.CONFIRMED);
                    booking.setPrice(booking.getPrice());
                    booking.setUpdatedAt(LocalDateTime.now());
                    
                    Ride ride = booking.getRide();
                    ride.setAvailableSeats(ride.getAvailableSeats() - booking.getSeats());
                    rideRepository.save(ride);
                    
                    return convertToDTO(bookingRepository.save(booking));
                })
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public BookingDTO rejectBookingPrice(Long bookingId, String rejectionNote) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setStatus(BookingStatus.REJECTED);
                    booking.setNegotiationNote(rejectionNote);
                    booking.setUpdatedAt(LocalDateTime.now());
                    return convertToDTO(bookingRepository.save(booking));
                })
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Transactional
    public BookingDTO updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);
        return convertToDTO(bookingRepository.save(booking));
    }

    public List<BookingDTO> getPassengerBookings(Long passengerId) {
        return bookingRepository.findByPassengerId(passengerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getRideBookings(Long rideId) {
        return bookingRepository.findByRideId(rideId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookingDTO getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed bookings can be cancelled");
        }

        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeats());
        rideRepository.save(ride);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public List<BookingDTO> findUpcomingBookings(LocalDateTime date) {
        return bookingRepository.findByBookingDateAfter(date)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> findActiveBookings() {
        return bookingRepository.findByStatus(BookingStatus.CONFIRMED)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setRideId(booking.getRide().getId());
        dto.setPassengerId(booking.getPassenger().getId());
        dto.setPassengerUsername(booking.getPassenger().getUser().getUsername());
        dto.setSeats(booking.getSeats());
        dto.setPrice(booking.getPrice());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getBookingDate());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setPickupLocation(booking.getPickupLocation());
        dto.setDropoffLocation(booking.getDropoffLocation());
        dto.setNegotiationNote(booking.getNegotiationNote());
        return dto;
    }
} 