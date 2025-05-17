package com.carpooling.api.service;

import com.carpooling.api.dto.RideDTO;
import com.carpooling.api.entity.Ride;
import com.carpooling.api.entity.Driver;
import com.carpooling.api.repository.RideRepository;
import com.carpooling.api.repository.DriverRepository;
import com.carpooling.api.enums.RideStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideService {
    private static final Logger logger = LoggerFactory.getLogger(RideService.class);
    
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public Ride createRide(Long driverId, RideDTO rideDTO) {
        logger.debug("Création d'un nouveau trajet par le conducteur {}", driverId);
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> {
                    logger.error("Conducteur non trouvé: {}", driverId);
                    return new RuntimeException("Driver not found");
                });

        try {
            Ride ride = new Ride();
            ride.setDriver(driver);
            ride.setDepartureLocation(rideDTO.getDepartureLocation());
            ride.setArrivalLocation(rideDTO.getArrivalLocation());
            ride.setDepartureTime(rideDTO.getDepartureTime());
            ride.setArrivalTime(rideDTO.getArrivalTime());
            ride.setTotalSeats(rideDTO.getTotalSeats());
            ride.setAvailableSeats(rideDTO.getTotalSeats());
            ride.setPricePerSeat(rideDTO.getPricePerSeat());
            ride.setStatus(RideStatus.SCHEDULED);
            ride.setDescription(rideDTO.getDescription());
            ride.setVehicleType(rideDTO.getVehicleType());
            ride.setVehicleModel(rideDTO.getVehicleModel());
            ride.setVehicleColor(rideDTO.getVehicleColor());
            ride.setVehiclePlate(rideDTO.getVehiclePlate());
            ride.setIsNegotiable(rideDTO.getIsNegotiable());
            ride.setDistance(0.0); // Valeur par défaut
            ride.setDuration(0); // Valeur par défaut

            return rideRepository.save(ride);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du trajet", e);
            throw new RuntimeException("Erreur lors de la création du trajet", e);
        }
    }

    @Transactional
    public Ride updateRideStatus(Long rideId, RideStatus status) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus(status);
        return rideRepository.save(ride);
    }

    public List<RideDTO> getPendingRides() {
        return rideRepository.findByStatus(RideStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RideDTO> getActiveRides() {
        return rideRepository.findByStatus(RideStatus.ACTIVE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RideDTO> getRidesByDriver(Long driverId) {
        return rideRepository.findByDriverId(driverId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RideDTO> getRideById(Long id) {
        return rideRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<RideDTO> getAllRides() {
        return rideRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RideDTO createRide(RideDTO rideDTO) {
        Ride ride = convertToEntity(rideDTO);
        return convertToDTO(rideRepository.save(ride));
    }

    @Transactional
    public RideDTO updateRide(Long id, RideDTO rideDTO) {
        Ride existingRide = rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        
        // Mettre à jour les champs
        existingRide.setDepartureLocation(rideDTO.getDepartureLocation());
        existingRide.setArrivalLocation(rideDTO.getArrivalLocation());
        existingRide.setDepartureTime(rideDTO.getDepartureTime());
        existingRide.setArrivalTime(rideDTO.getArrivalTime());
        existingRide.setTotalSeats(rideDTO.getTotalSeats());
        existingRide.setAvailableSeats(rideDTO.getAvailableSeats());
        existingRide.setPricePerSeat(rideDTO.getPricePerSeat());
        existingRide.setStatus(rideDTO.getStatus());

        return convertToDTO(rideRepository.save(existingRide));
    }

    @Transactional
    public void deleteRide(Long id) {
        rideRepository.deleteById(id);
    }

    private RideDTO convertToDTO(Ride ride) {
        RideDTO dto = new RideDTO();
        dto.setId(ride.getId());
        dto.setDriverId(ride.getDriver().getId());
        dto.setDriverUsername(ride.getDriver().getUser().getUsername());
        dto.setDepartureLocation(ride.getDepartureLocation());
        dto.setArrivalLocation(ride.getArrivalLocation());
        dto.setDepartureTime(ride.getDepartureTime());
        dto.setArrivalTime(ride.getArrivalTime());
        dto.setTotalSeats(ride.getTotalSeats());
        dto.setAvailableSeats(ride.getAvailableSeats());
        dto.setPricePerSeat(ride.getPricePerSeat());
        dto.setStatus(ride.getStatus());
        dto.setDescription(ride.getDescription());
        dto.setVehicleType(ride.getVehicleType());
        dto.setVehicleModel(ride.getVehicleModel());
        dto.setVehicleColor(ride.getVehicleColor());
        dto.setVehiclePlate(ride.getVehiclePlate());
        dto.setDistance(ride.getDistance());
        dto.setDuration(ride.getDuration());
        dto.setIsNegotiable(ride.getIsNegotiable());
        return dto;
    }

    private Ride convertToEntity(RideDTO dto) {
        Ride ride = new Ride();
        ride.setDepartureLocation(dto.getDepartureLocation());
        ride.setArrivalLocation(dto.getArrivalLocation());
        ride.setDepartureTime(dto.getDepartureTime());
        ride.setArrivalTime(dto.getArrivalTime());
        ride.setTotalSeats(dto.getTotalSeats());
        ride.setAvailableSeats(dto.getAvailableSeats());
        ride.setPricePerSeat(dto.getPricePerSeat());
        ride.setStatus(dto.getStatus());
        return ride;
    }

    public List<Ride> searchRides(String departureLocation, String arrivalLocation, LocalDateTime departureTime) {
        return rideRepository.findByDepartureLocationAndArrivalLocationAndDepartureTimeAfter(
            departureLocation,
            arrivalLocation,
            departureTime
        );
    }

    public List<Ride> getDriverRides(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public List<Ride> findRides(String departure, String arrival, LocalDateTime date) {
        return rideRepository.findByDepartureLocationAndArrivalLocationAndDepartureTimeAfter(
            departure, arrival, date
        );
    }
} 