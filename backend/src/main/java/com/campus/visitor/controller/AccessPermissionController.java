package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.AccessPermission;
import com.campus.visitor.service.AccessPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access-permissions")
@CrossOrigin(origins = "*")
public class AccessPermissionController {
    
    @Autowired
    private AccessPermissionService accessPermissionService;

    @GetMapping
    public ApiResponse<List<AccessPermission>> getAllPermissions() {
        return ApiResponse.success(accessPermissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    public ApiResponse<AccessPermission> getPermissionById(@PathVariable Long id) {
        AccessPermission perm = accessPermissionService.getPermissionById(id);
        if (perm == null) {
            return ApiResponse.error("权限不存在", 404);
        }
        return ApiResponse.success(perm);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ApiResponse<List<AccessPermission>> getPermissionsByAppointmentId(@PathVariable Long appointmentId) {
        return ApiResponse.success(accessPermissionService.getPermissionsByAppointmentId(appointmentId));
    }

    @PostMapping("/validate")
    public ApiResponse<Map<String, Object>> validateAccess(@RequestBody Map<String, Object> request) {
        Long appointmentId = request.containsKey("appointmentId") ? 
            Long.valueOf(request.get("appointmentId").toString()) : null;
        String accessArea = (String) request.get("accessArea");
        
        if (appointmentId == null) {
            return ApiResponse.error("预约ID不能为空");
        }
        
        boolean canAccess = accessPermissionService.canAccessNow(appointmentId, accessArea);
        Map<String, Object> result = new HashMap<>();
        result.put("canAccess", canAccess);
        result.put("currentTime", LocalDateTime.now().toString());
        result.put("accessArea", accessArea);
        
        if (canAccess) {
            return ApiResponse.success("可以访问", result);
        } else {
            return ApiResponse.error("无访问权限或不在有效时间内", 403);
        }
    }

    @PostMapping("/deactivate/{appointmentId}")
    public ApiResponse<Void> deactivatePermissions(@PathVariable Long appointmentId) {
        accessPermissionService.deactivatePermissions(appointmentId);
        return ApiResponse.success("权限已停用", null);
    }
}
