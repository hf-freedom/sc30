package com.campus.visitor.service;

import com.campus.visitor.entity.AuditLog;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {
    
    @Autowired
    private InMemoryStorage storage;

    public void createLog(Long appointmentId, String actionType, 
                           String actionDescription, String visitorName, 
                           String visitorPhone, String accessArea, String operator) {
        AuditLog log = new AuditLog();
        log.setId(storage.getLogIdCounter().getAndIncrement());
        log.setAppointmentId(appointmentId);
        log.setActionType(actionType);
        log.setActionDescription(actionDescription);
        log.setVisitorName(visitorName);
        log.setVisitorPhone(visitorPhone);
        log.setAccessArea(accessArea);
        log.setActionTime(LocalDateTime.now());
        log.setOperator(operator);
        
        storage.getAuditLogs().put(log.getId(), log);
    }

    public List<AuditLog> getAllLogs() {
        return storage.getAuditLogs().values().stream()
            .sorted(Comparator.comparing(AuditLog::getActionTime).reversed())
            .collect(Collectors.toList());
    }

    public List<AuditLog> getLogsByAppointmentId(Long appointmentId) {
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog log : storage.getAuditLogs().values()) {
            if (appointmentId.equals(log.getAppointmentId())) {
                result.add(log);
            }
        }
        result.sort(Comparator.comparing(AuditLog::getActionTime).reversed());
        return result;
    }

    public List<AuditLog> getLogsByActionType(String actionType) {
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog log : storage.getAuditLogs().values()) {
            if (actionType.equals(log.getActionType())) {
                result.add(log);
            }
        }
        result.sort(Comparator.comparing(AuditLog::getActionTime).reversed());
        return result;
    }

    public List<AuditLog> getLogsByVisitorName(String visitorName) {
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog log : storage.getAuditLogs().values()) {
            if (log.getVisitorName() != null && log.getVisitorName().contains(visitorName)) {
                result.add(log);
            }
        }
        result.sort(Comparator.comparing(AuditLog::getActionTime).reversed());
        return result;
    }
}
