package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.dto.AppointmentRequest;
import com.campus.visitor.entity.Appointment;
import com.campus.visitor.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public ApiResponse<List<Appointment>> getAllAppointments() {
        return ApiResponse.success(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ApiResponse<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ApiResponse.error("预约不存在", 404);
        }
        return ApiResponse.success(appointment);
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<Appointment>> getAppointmentsByEmployeeId(@PathVariable Long employeeId) {
        return ApiResponse.success(appointmentService.getAppointmentsByEmployeeId(employeeId));
    }

    @PostMapping
    public ApiResponse<Appointment> createAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.createAppointment(request);
            return ApiResponse.success("预约创建成功", appointment);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Appointment> approveAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            Long approverId = body != null && body.containsKey("approverId") ? 
                Long.valueOf(body.get("approverId").toString()) : null;
            String comment = body != null && body.containsKey("comment") ? 
                (String) body.get("comment") : "";
            
            if (approverId == null) {
                return ApiResponse.error("审批人ID不能为空");
            }
            
            Appointment appointment = appointmentService.approveAppointment(id, approverId, comment);
            return ApiResponse.success("预约审批通过", appointment);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Appointment> rejectAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            Long approverId = body != null && body.containsKey("approverId") ? 
                Long.valueOf(body.get("approverId").toString()) : null;
            String reason = body != null && body.containsKey("reason") ? 
                (String) body.get("reason") : "审批拒绝";
            
            if (approverId == null) {
                return ApiResponse.error("审批人ID不能为空");
            }
            
            Appointment appointment = appointmentService.rejectAppointment(id, approverId, reason);
            return ApiResponse.success("预约已拒绝", appointment);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null && body.containsKey("reason") ? 
            body.get("reason") : "用户取消";
        appointmentService.cancelAppointment(id, reason);
        return ApiResponse.success("预约已取消", null);
    }

    @PostMapping("/{id}/checkin")
    public ApiResponse<Appointment> checkIn(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.checkIn(id);
            return ApiResponse.success("签到成功", appointment);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/checkout")
    public ApiResponse<Appointment> checkOut(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.checkOut(id);
            return ApiResponse.success("签退成功", appointment);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/overdue")
    public ApiResponse<List<Appointment>> getOverdueAppointments() {
        return ApiResponse.success(appointmentService.getOverdueAppointments());
    }
}
