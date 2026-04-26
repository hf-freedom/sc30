package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.AuditLog;
import com.campus.visitor.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {
    
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public ApiResponse<List<AuditLog>> getAllLogs() {
        return ApiResponse.success(auditLogService.getAllLogs());
    }

    @GetMapping("/appointment/{appointmentId}")
    public ApiResponse<List<AuditLog>> getLogsByAppointmentId(@PathVariable Long appointmentId) {
        return ApiResponse.success(auditLogService.getLogsByAppointmentId(appointmentId));
    }

    @GetMapping("/action-type/{actionType}")
    public ApiResponse<List<AuditLog>> getLogsByActionType(@PathVariable String actionType) {
        return ApiResponse.success(auditLogService.getLogsByActionType(actionType));
    }

    @GetMapping("/visitor/{visitorName}")
    public ApiResponse<List<AuditLog>> getLogsByVisitorName(@PathVariable String visitorName) {
        return ApiResponse.success(auditLogService.getLogsByVisitorName(visitorName));
    }
}
