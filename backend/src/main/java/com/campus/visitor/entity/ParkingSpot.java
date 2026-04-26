package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ParkingSpot implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String spotNumber;
    private String location;
    private ParkingSpotStatus status;
    private Long currentAppointmentId;
    private LocalDateTime occupiedSince;
    private LocalDateTime expectedReleaseTime;
    
    public enum ParkingSpotStatus {
        AVAILABLE,
        OCCUPIED,
        RESERVED,
        MAINTENANCE
    }
}
