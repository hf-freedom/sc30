package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long appointmentId;
    private String actionType;
    private String actionDescription;
    private String visitorName;
    private String visitorPhone;
    private String accessArea;
    private LocalDateTime actionTime;
    private String operator;
}
