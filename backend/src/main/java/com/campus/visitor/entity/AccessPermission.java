package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AccessPermission implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long appointmentId;
    private String accessArea;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
}
