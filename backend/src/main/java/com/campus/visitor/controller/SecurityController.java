package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.SecurityAlert;
import com.campus.visitor.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@CrossOrigin(origins = "*")
public class SecurityController {
    
    @Autowired
    private SecurityService securityService;

    @GetMapping("/alerts")
    public ApiResponse<List<SecurityAlert>> getAllAlerts() {
        return ApiResponse.success(securityService.getAllAlerts());
    }

    @GetMapping("/alerts/pending")
    public ApiResponse<List<SecurityAlert>> getPendingAlerts() {
        return ApiResponse.success(securityService.getPendingAlerts());
    }

    @GetMapping("/alerts/{id}")
    public ApiResponse<SecurityAlert> getAlertById(@PathVariable Long id) {
        SecurityAlert alert = securityService.getAlertById(id);
        if (alert == null) {
            return ApiResponse.error("提醒不存在", 404);
        }
        return ApiResponse.success(alert);
    }

    @PostMapping("/alerts/{id}/handle")
    public ApiResponse<SecurityAlert> handleAlert(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String handlerName = body != null && body.containsKey("handlerName") ? 
            body.get("handlerName") : "安保人员";
        String action = body != null && body.containsKey("action") ? 
            body.get("action") : "HANDLED";
        
        SecurityAlert alert = securityService.handleAlert(id, handlerName, action);
        if (alert == null) {
            return ApiResponse.error("提醒不存在", 404);
        }
        return ApiResponse.success("提醒已处理", alert);
    }

    @PostMapping("/alerts/check-overdue")
    public ApiResponse<String> checkOverdueVisitors() {
        securityService.checkAndCreateOverdueAlerts();
        return ApiResponse.success("已检查并生成超时提醒", "完成");
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getSecurityDashboard() {
        List<SecurityAlert> allAlerts = securityService.getAllAlerts();
        List<SecurityAlert> pendingAlerts = securityService.getPendingAlerts();
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalAlerts", allAlerts.size());
        dashboard.put("pendingAlerts", pendingAlerts.size());
        dashboard.put("handledAlerts", allAlerts.size() - pendingAlerts.size());
        
        return ApiResponse.success(dashboard);
    }
}
