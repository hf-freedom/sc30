package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SecurityAlert implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long appointmentId;
    private String alertType;
    private String alertMessage;
    private LocalDateTime alertTime;
    private SecurityAlertStatus status;
    private String handledBy;
    private LocalDateTime handledTime;
    
    public enum SecurityAlertStatus {
        PENDING,
        HANDLED,
        DISMISSED
    }
}
