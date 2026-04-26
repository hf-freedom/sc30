package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.VisitorPass;
import com.campus.visitor.service.VisitorPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/passes")
@CrossOrigin(origins = "*")
public class VisitorPassController {
    
    @Autowired
    private VisitorPassService visitorPassService;

    @GetMapping
    public ApiResponse<List<VisitorPass>> getAllPasses() {
        return ApiResponse.success(visitorPassService.getAllPasses());
    }

    @GetMapping("/{id}")
    public ApiResponse<VisitorPass> getPassById(@PathVariable Long id) {
        VisitorPass pass = visitorPassService.getPassById(id);
        if (pass == null) {
            return ApiResponse.error("通行证不存在", 404);
        }
        return ApiResponse.success(pass);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ApiResponse<VisitorPass> getPassByAppointmentId(@PathVariable Long appointmentId) {
        VisitorPass pass = visitorPassService.getPassByAppointmentId(appointmentId);
        if (pass == null) {
            return ApiResponse.error("通行证不存在", 404);
        }
        return ApiResponse.success(pass);
    }

    @GetMapping("/code/{passCode}")
    public ApiResponse<VisitorPass> getPassByCode(@PathVariable String passCode) {
        VisitorPass pass = visitorPassService.getPassByCode(passCode);
        if (pass == null) {
            return ApiResponse.error("通行证不存在", 404);
        }
        return ApiResponse.success(pass);
    }

    @PostMapping("/validate")
    public ApiResponse<Map<String, Object>> validatePass(@RequestBody Map<String, String> request) {
        String passCode = request.get("passCode");
        String accessArea = request.get("accessArea");
        
        boolean valid = visitorPassService.validatePass(passCode, accessArea);
        Map<String, Object> result = new HashMap<>();
        result.put("valid", valid);
        
        if (valid) {
            VisitorPass pass = visitorPassService.getPassByCode(passCode);
            result.put("pass", pass);
            return ApiResponse.success("验证通过", result);
        } else {
            return ApiResponse.error("验证失败", 401);
        }
    }
}
