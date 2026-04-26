package com.campus.visitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    private String visitorName;
    private String visitorPhone;
    private String visitorIdCard;
    private String vehicleInfo;
    private Long employeeId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private String accessArea;
    private Boolean isDriving;
    private Long meetingRoomId;
    private String purpose;
}
