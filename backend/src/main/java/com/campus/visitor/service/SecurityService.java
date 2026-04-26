package com.campus.visitor.service;

import com.campus.visitor.entity.Appointment;
import com.campus.visitor.entity.SecurityAlert;
import com.campus.visitor.entity.Visitor;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityService {
    
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private AuditLogService auditLogService;

    public List<SecurityAlert> getAllAlerts() {
        return storage.getSecurityAlerts().values().stream()
            .sorted(Comparator.comparing(SecurityAlert::getAlertTime).reversed())
            .collect(Collectors.toList());
    }

    public List<SecurityAlert> getPendingAlerts() {
        List<SecurityAlert> result = new ArrayList<>();
        for (SecurityAlert alert : storage.getSecurityAlerts().values()) {
            if (alert.getStatus() == SecurityAlert.SecurityAlertStatus.PENDING) {
                result.add(alert);
            }
        }
        result.sort(Comparator.comparing(SecurityAlert::getAlertTime).reversed());
        return result;
    }

    public SecurityAlert getAlertById(Long id) {
        return storage.getSecurityAlerts().get(id);
    }

    public SecurityAlert createOverdueAlert(Appointment appointment) {
        for (SecurityAlert existing : storage.getSecurityAlerts().values()) {
            if (appointment.getId().equals(existing.getAppointmentId()) &&
                existing.getStatus() == SecurityAlert.SecurityAlertStatus.PENDING &&
                "OVERDUE".equals(existing.getAlertType())) {
                return existing;
            }
        }

        Visitor visitor = visitorService.getVisitorById(appointment.getVisitorId());

        SecurityAlert alert = new SecurityAlert();
        alert.setId(storage.getAlertIdCounter().getAndIncrement());
        alert.setAppointmentId(appointment.getId());
        alert.setAlertType("OVERDUE");
        alert.setAlertMessage("访客超时未离园: " + (visitor != null ? visitor.getName() : "未知") + 
            ", 预约结束时间: " + appointment.getEndTime());
        alert.setAlertTime(LocalDateTime.now());
        alert.setStatus(SecurityAlert.SecurityAlertStatus.PENDING);

        storage.getSecurityAlerts().put(alert.getId(), alert);

        auditLogService.createLog(appointment.getId(), "SECURITY_ALERT",
            "生成超时未离园安保提醒",
            visitor != null ? visitor.getName() : null,
            visitor != null ? visitor.getPhoneNumber() : null,
            appointment.getAccessArea(), "安保系统");

        return alert;
    }

    public void checkAndCreateOverdueAlerts() {
        List<Appointment> overdueAppointments = appointmentService.getOverdueAppointments();
        for (Appointment appointment : overdueAppointments) {
            createOverdueAlert(appointment);
        }
    }

    public SecurityAlert handleAlert(Long alertId, String handlerName, String action) {
        SecurityAlert alert = storage.getSecurityAlerts().get(alertId);
        if (alert == null) {
            return null;
        }

        alert.setStatus("DISMISSED".equals(action) ? 
            SecurityAlert.SecurityAlertStatus.DISMISSED : 
            SecurityAlert.SecurityAlertStatus.HANDLED);
        alert.setHandledBy(handlerName);
        alert.setHandledTime(LocalDateTime.now());

        storage.getSecurityAlerts().put(alert.getId(), alert);

        Appointment appointment = appointmentService.getAppointmentById(alert.getAppointmentId());
        if (appointment != null) {
            auditLogService.createLog(appointment.getId(), "ALERT_HANDLED",
                "安保提醒已处理: " + alert.getAlertMessage() + ", 处理人: " + handlerName,
                appointment.getVisitorId() != null ? 
                    visitorService.getVisitorById(appointment.getVisitorId()).getName() : null,
                null, appointment.getAccessArea(), handlerName);
        }

        return alert;
    }
}
