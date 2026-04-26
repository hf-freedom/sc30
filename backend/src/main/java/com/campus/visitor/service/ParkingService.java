package com.campus.visitor.service;

import com.campus.visitor.entity.ParkingSpot;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingService {
    
    @Autowired
    private InMemoryStorage storage;

    public List<ParkingSpot> getAllParkingSpots() {
        return new ArrayList<>(storage.getParkingSpots().values());
    }

    public List<ParkingSpot> getAvailableParkingSpots() {
        List<ParkingSpot> result = new ArrayList<>();
        for (ParkingSpot spot : storage.getParkingSpots().values()) {
            if (spot.getStatus() == ParkingSpot.ParkingSpotStatus.AVAILABLE) {
                result.add(spot);
            }
        }
        return result;
    }

    public ParkingSpot getParkingSpotById(Long id) {
        return storage.getParkingSpots().get(id);
    }

    public ParkingSpot getParkingSpotByAppointmentId(Long appointmentId) {
        for (ParkingSpot spot : storage.getParkingSpots().values()) {
            if (appointmentId.equals(spot.getCurrentAppointmentId())) {
                return spot;
            }
        }
        return null;
    }

    public ParkingSpot allocateParkingSpot(Long appointmentId, 
                                            LocalDateTime expectedStartTime, 
                                            LocalDateTime expectedEndTime) {
        List<ParkingSpot> availableSpots = getAvailableParkingSpots();
        if (availableSpots.isEmpty()) {
            return null;
        }

        ParkingSpot spot = availableSpots.get(0);
        spot.setStatus(ParkingSpot.ParkingSpotStatus.RESERVED);
        spot.setCurrentAppointmentId(appointmentId);
        spot.setExpectedReleaseTime(expectedEndTime);
        spot.setOccupiedSince(expectedStartTime);

        storage.getParkingSpots().put(spot.getId(), spot);
        return spot;
    }

    public void occupyParkingSpot(Long appointmentId) {
        ParkingSpot spot = getParkingSpotByAppointmentId(appointmentId);
        if (spot != null && spot.getStatus() == ParkingSpot.ParkingSpotStatus.RESERVED) {
            spot.setStatus(ParkingSpot.ParkingSpotStatus.OCCUPIED);
            spot.setOccupiedSince(LocalDateTime.now());
            storage.getParkingSpots().put(spot.getId(), spot);
        }
    }

    public void releaseParkingSpot(Long appointmentId) {
        ParkingSpot spot = getParkingSpotByAppointmentId(appointmentId);
        if (spot != null) {
            spot.setStatus(ParkingSpot.ParkingSpotStatus.AVAILABLE);
            spot.setCurrentAppointmentId(null);
            spot.setOccupiedSince(null);
            spot.setExpectedReleaseTime(null);
            storage.getParkingSpots().put(spot.getId(), spot);
        }
    }

    public ParkingSpot createParkingSpot(ParkingSpot parkingSpot) {
        long id = storage.getParkingSpotIdCounter().getAndIncrement();
        parkingSpot.setId(id);
        storage.getParkingSpots().put(id, parkingSpot);
        return parkingSpot;
    }
}
