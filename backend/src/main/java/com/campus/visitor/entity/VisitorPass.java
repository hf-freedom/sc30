package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class VisitorPass implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long appointmentId;
    private String passCode;
    private String qrCode;
    private Boolean isActive;
    private LocalDateTime effectiveTime;
    private LocalDateTime expireTime;
    private String accessAreas;
}
