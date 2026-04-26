package com.campus.visitor.task;

import com.campus.visitor.service.AppointmentService;
import com.campus.visitor.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SecurityTask {
    
    @Autowired
    private SecurityService securityService;

    @Autowired
    private AppointmentService appointmentService;

    @Scheduled(fixedRate = 60000)
    public void checkOverdueVisitors() {
        securityService.checkAndCreateOverdueAlerts();
    }

    @Scheduled(fixedRate = 300000)
    public void checkEmployeeStatusAndCancelAppointments() {
        appointmentService.checkAndCancelInvalidAppointments();
    }
}
