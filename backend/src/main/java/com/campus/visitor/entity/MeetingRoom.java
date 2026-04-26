package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class MeetingRoom implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private MeetingRoomStatus status;
    private Long currentAppointmentId;
    
    public enum MeetingRoomStatus {
        AVAILABLE,
        OCCUPIED,
        MAINTENANCE
    }
}
