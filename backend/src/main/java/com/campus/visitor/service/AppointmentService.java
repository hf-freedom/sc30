package com.campus.visitor.service;

import com.campus.visitor.entity.*;
import com.campus.visitor.dto.AppointmentRequest;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VisitorPassService visitorPassService;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private AccessPermissionService accessPermissionService;

    @Autowired
    private AuditLogService auditLogService;

    public List<Appointment> getAllAppointments() {
        return storage.getAppointments().values().stream()
            .sorted(Comparator.comparing(Appointment::getCreateTime).reversed())
            .collect(Collectors.toList());
    }

    public Appointment getAppointmentById(Long id) {
        return storage.getAppointments().get(id);
    }

    public List<Appointment> getAppointmentsByEmployeeId(Long employeeId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : storage.getAppointments().values()) {
            if (employeeId.equals(appointment.getEmployeeId())) {
                result.add(appointment);
            }
        }
        result.sort(Comparator.comparing(Appointment::getCreateTime).reversed());
        return result;
    }

    public Appointment createAppointment(AppointmentRequest request) {
        Visitor visitor = new Visitor();
        visitor.setName(request.getVisitorName());
        visitor.setPhoneNumber(request.getVisitorPhone());
        visitor.setIdCardNumber(request.getVisitorIdCard());
        visitor.setVehicleInfo(request.getVehicleInfo());
        visitor.setIsBlacklisted(false);
        visitor = visitorService.createVisitor(visitor);

        if (visitorService.isBlacklisted(visitor.getId())) {
            throw new RuntimeException("该访客在黑名单中，无法预约");
        }

        Employee employee = employeeService.getEmployeeById(request.getEmployeeId());
        if (employee == null) {
            throw new RuntimeException("被访人不存在");
        }

        if (!employeeService.isActive(request.getEmployeeId())) {
            throw new RuntimeException("被访人状态异常，无法预约");
        }

        Appointment appointment = new Appointment();
        appointment.setId(storage.getAppointmentIdCounter().getAndIncrement());
        appointment.setVisitorId(visitor.getId());
        appointment.setEmployeeId(request.getEmployeeId());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setAccessArea(request.getAccessArea());
        appointment.setIsDriving(request.getIsDriving());
        appointment.setMeetingRoomId(request.getMeetingRoomId());
        appointment.setPurpose(request.getPurpose());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING_APPROVAL);
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        if (request.getMeetingRoomId() != null) {
            if (!meetingRoomService.isAvailable(request.getMeetingRoomId())) {
                throw new RuntimeException("会议室已被占用");
            }
        }

        storage.getAppointments().put(appointment.getId(), appointment);

        auditLogService.createLog(appointment.getId(), "APPOINTMENT_CREATE",
            "访客预约申请已提交: " + visitor.getName() + " 访问 " + employee.getName(),
            visitor.getName(), visitor.getPhoneNumber(), appointment.getAccessArea(), "系统");

        return appointment;
    }

    public Appointment approveAppointment(Long appointmentId, Long approverId, String comment) {
        Appointment appointment = storage.getAppointments().get(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING_APPROVAL) {
            throw new RuntimeException("预约状态不可审批");
        }

        if (!appointment.getEmployeeId().equals(approverId) && !employeeService.canApproveVisitor(approverId)) {
            throw new RuntimeException("无审批权限");
        }

        if (!employeeService.isActive(appointment.getEmployeeId())) {
            throw new RuntimeException("被访人状态异常，预约自动取消");
        }

        if (appointment.getIsDriving() != null && appointment.getIsDriving()) {
            ParkingSpot spot = parkingService.allocateParkingSpot(appointment.getId(), 
                appointment.getStartTime(), appointment.getEndTime());
            if (spot == null) {
                throw new RuntimeException("暂无可用停车位");
            }
        }

        accessPermissionService.createPermission(appointment);

        visitorPassService.createPass(appointment);

        appointment.setStatus(Appointment.AppointmentStatus.APPROVED);
        appointment.setUpdateTime(LocalDateTime.now());
        storage.getAppointments().put(appointment.getId(), appointment);

        Visitor visitor = visitorService.getVisitorById(appointment.getVisitorId());
        Employee employee = employeeService.getEmployeeById(appointment.getEmployeeId());

        auditLogService.createLog(appointment.getId(), "APPOINTMENT_APPROVE",
            "预约已批准: " + visitor.getName() + " 访问 " + employee.getName(),
            visitor.getName(), visitor.getPhoneNumber(), appointment.getAccessArea(), 
            employee != null ? employee.getName() : "系统");

        return appointment;
    }

    public Appointment rejectAppointment(Long appointmentId, Long approverId, String reason) {
        Appointment appointment = storage.getAppointments().get(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING_APPROVAL) {
            throw new RuntimeException("预约状态不可审批");
        }

        if (!appointment.getEmployeeId().equals(approverId) && !employeeService.canApproveVisitor(approverId)) {
            throw new RuntimeException("无审批权限");
        }

        appointment.setStatus(Appointment.AppointmentStatus.REJECTED);
        appointment.setUpdateTime(LocalDateTime.now());
        storage.getAppointments().put(appointment.getId(), appointment);

        Visitor visitor = visitorService.getVisitorById(appointment.getVisitorId());
        Employee employee = employeeService.getEmployeeById(approverId);

        auditLogService.createLog(appointment.getId(), "APPOINTMENT_REJECT",
            "预约已拒绝: " + visitor.getName() + ", 原因: " + reason,
            visitor.getName(), visitor.getPhoneNumber(), appointment.getAccessArea(),
            employee != null ? employee.getName() : "系统");

        return appointment;
    }

    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = storage.getAppointments().get(appointmentId);
        if (appointment == null) {
            return;
        }

        Appointment.AppointmentStatus status = appointment.getStatus();
        if (status == Appointment.AppointmentStatus.COMPLETED || 
            status == Appointment.AppointmentStatus.CANCELLED) {
            return;
        }

        if (status == Appointment.AppointmentStatus.APPROVED || 
            status == Appointment.AppointmentStatus.IN_PROGRESS) {
            parkingService.releaseParkingSpot(appointment.getId());
            if (appointment.getMeetingRoomId() != null) {
                meetingRoomService.releaseMeetingRoom(appointment.getMeetingRoomId(), appointment.getId());
            }
            accessPermissionService.deactivatePermissions(appointment.getId());
            visitorPassService.deactivatePass(appointment.getId());
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setUpdateTime(LocalDateTime.now());
        storage.getAppointments().put(appointment.getId(), appointment);

        Visitor visitor = visitorService.getVisitorById(appointment.getVisitorId());

        auditLogService.createLog(appointment.getId(), "APPOINTMENT_CANCEL",
            "预约已取消: " + reason,
            visitor != null ? visitor.getName() : null,
            visitor != null ? visitor.getPhoneNumber() : null,
            appointment.getAccessArea(), "系统");
    }

    public Appointment checkIn(Long appointmentId) {
        Appointment appointment = storage.getAppointments().get(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.APPROVED) {
            throw new RuntimeException("预约状态不可签到");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(appointment.getStartTime())) {
            throw new RuntimeException("提前到达，暂无法刷门禁进入");
        }

        if (now.isAfter(appointment.getEndTime())) {
            throw new RuntimeException("预约已过期");
        }

        if (appointment.getMeetingRoomId() != null) {
            boolean occupied = meetingRoomService.occupyMeetingRoom(appointment.getMeetingRoomId(), appointment.getId());
            if (!occupied) {
                throw new RuntimeException("会议室占用失败，可能已被他人占用");
            }
        }

        appointment.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);
        appointment.setUpdateTime(now);
        storage.getAppointments().put(appointment.getId(), appointment);

        Visitor visitor = visitorService.getVisitorById(appointment.getVisitorId());

        auditLogService.createLog(appointment.getId(), "CHECK_IN",
            "访客入园: " + visitor.getName() + 
            (appointment.getMeetingRoomId() != null ? "，会议室已占用" : ""),
            visitor.getName(), visitor.getPhoneNumber(), appointment.getAccessArea(), "门禁系统");

        return appointment;
    }

    public Appointment checkOut(Long appointmentId) {
        Appointment appointment = storage.getAppointments().get(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.IN_PROGRESS) {
            throw new RuntimeException("预约状态不可签退");
        }

        parkingService.releaseParkingSpot(appointment.getId());
        if (appointment.getMeetingRoomId() != null) {
            meetingRoomService.releaseMeetingRoom(appointment.getMeetingRoomId(), appointment.getId());
        }
        accessPermissionService.deactivatePermissions(appointment.getId());
        visitorPassService.deactivatePass(appointment.getId());

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setUpdateTime(LocalDateTime.now());
        storage.getAppointments().put(appointment.getId(), appointment);

        Visitor visitor = visitorService.getVisitorById(appointment.getVisitorId());

        auditLogService.createLog(appointment.getId(), "CHECK_OUT",
            "访客离园: " + visitor.getName(),
            visitor.getName(), visitor.getPhoneNumber(), appointment.getAccessArea(), "门禁系统");

        return appointment;
    }

    public void checkAndCancelInvalidAppointments() {
        LocalDateTime now = LocalDateTime.now();
        for (Appointment appointment : storage.getAppointments().values()) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.PENDING_APPROVAL ||
                appointment.getStatus() == Appointment.AppointmentStatus.APPROVED) {
                if (now.isBefore(appointment.getStartTime())) {
                    Employee employee = employeeService.getEmployeeById(appointment.getEmployeeId());
                    if (employee != null && employee.getStatus() != Employee.EmployeeStatus.ACTIVE) {
                        cancelAppointment(appointment.getId(), "被访人状态异常");
                    }
                }
            }
        }
    }

    public List<Appointment> getOverdueAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : storage.getAppointments().values()) {
            if (appointment.getStatus() == Appointment.AppointmentStatus.IN_PROGRESS &&
                now.isAfter(appointment.getEndTime())) {
                result.add(appointment);
            }
        }
        return result;
    }
}
