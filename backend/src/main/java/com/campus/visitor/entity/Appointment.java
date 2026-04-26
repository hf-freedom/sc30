package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long visitorId;
    private Long employeeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String accessArea;
    private Boolean isDriving;
    private Long meetingRoomId;
    private String purpose;
    private AppointmentStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    public enum AppointmentStatus {
        PENDING_APPROVAL,
        APPROVED,
        REJECTED,
        CANCELLED,
        IN_PROGRESS,
        COMPLETED,
        EXPIRED
    }
}
