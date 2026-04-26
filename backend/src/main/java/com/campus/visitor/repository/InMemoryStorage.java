package com.campus.visitor.repository;

import com.campus.visitor.entity.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    private final ConcurrentHashMap<Long, Employee> employees = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Visitor> visitors = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Appointment> appointments = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, VisitorPass> visitorPasses = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ParkingSpot> parkingSpots = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, MeetingRoom> meetingRooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AccessPermission> accessPermissions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, SecurityAlert> securityAlerts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AuditLog> auditLogs = new ConcurrentHashMap<>();

    private final AtomicLong employeeIdCounter = new AtomicLong(1);
    private final AtomicLong visitorIdCounter = new AtomicLong(1);
    private final AtomicLong appointmentIdCounter = new AtomicLong(1);
    private final AtomicLong passIdCounter = new AtomicLong(1);
    private final AtomicLong parkingSpotIdCounter = new AtomicLong(1);
    private final AtomicLong meetingRoomIdCounter = new AtomicLong(1);
    private final AtomicLong permissionIdCounter = new AtomicLong(1);
    private final AtomicLong alertIdCounter = new AtomicLong(1);
    private final AtomicLong logIdCounter = new AtomicLong(1);

    public InMemoryStorage() {
        initializeTestData();
    }

    private void initializeTestData() {
        // 初始化测试员工
        Employee emp1 = new Employee();
        emp1.setId(employeeIdCounter.getAndIncrement());
        emp1.setName("张三");
        emp1.setDepartment("技术部");
        emp1.setCanApproveVisitor(true);
        emp1.setStatus(Employee.EmployeeStatus.ACTIVE);
        employees.put(emp1.getId(), emp1);

        Employee emp2 = new Employee();
        emp2.setId(employeeIdCounter.getAndIncrement());
        emp2.setName("李四");
        emp2.setDepartment("市场部");
        emp2.setCanApproveVisitor(true);
        emp2.setStatus(Employee.EmployeeStatus.ACTIVE);
        employees.put(emp2.getId(), emp2);

        Employee emp3 = new Employee();
        emp3.setId(employeeIdCounter.getAndIncrement());
        emp3.setName("王五");
        emp3.setDepartment("人事部");
        emp3.setCanApproveVisitor(false);
        emp3.setStatus(Employee.EmployeeStatus.ON_LEAVE);
        employees.put(emp3.getId(), emp3);

        // 初始化测试停车位
        for (int i = 1; i <= 20; i++) {
            ParkingSpot spot = new ParkingSpot();
            spot.setId(parkingSpotIdCounter.getAndIncrement());
            spot.setSpotNumber("P-" + String.format("%02d", i));
            spot.setLocation("地下一层停车场");
            spot.setStatus(ParkingSpot.ParkingSpotStatus.AVAILABLE);
            parkingSpots.put(spot.getId(), spot);
        }

        // 初始化测试会议室
        MeetingRoom room1 = new MeetingRoom();
        room1.setId(meetingRoomIdCounter.getAndIncrement());
        room1.setName("第一会议室");
        room1.setLocation("1楼");
        room1.setCapacity(10);
        room1.setStatus(MeetingRoom.MeetingRoomStatus.AVAILABLE);
        meetingRooms.put(room1.getId(), room1);

        MeetingRoom room2 = new MeetingRoom();
        room2.setId(meetingRoomIdCounter.getAndIncrement());
        room2.setName("第二会议室");
        room2.setLocation("2楼");
        room2.setCapacity(20);
        room2.setStatus(MeetingRoom.MeetingRoomStatus.AVAILABLE);
        meetingRooms.put(room2.getId(), room2);

        MeetingRoom room3 = new MeetingRoom();
        room3.setId(meetingRoomIdCounter.getAndIncrement());
        room3.setName("第三会议室");
        room3.setLocation("3楼");
        room3.setCapacity(30);
        room3.setStatus(MeetingRoom.MeetingRoomStatus.AVAILABLE);
        meetingRooms.put(room3.getId(), room3);
    }

    public ConcurrentHashMap<Long, Employee> getEmployees() {
        return employees;
    }

    public ConcurrentHashMap<Long, Visitor> getVisitors() {
        return visitors;
    }

    public ConcurrentHashMap<Long, Appointment> getAppointments() {
        return appointments;
    }

    public ConcurrentHashMap<Long, VisitorPass> getVisitorPasses() {
        return visitorPasses;
    }

    public ConcurrentHashMap<Long, ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public ConcurrentHashMap<Long, MeetingRoom> getMeetingRooms() {
        return meetingRooms;
    }

    public ConcurrentHashMap<Long, AccessPermission> getAccessPermissions() {
        return accessPermissions;
    }

    public ConcurrentHashMap<Long, SecurityAlert> getSecurityAlerts() {
        return securityAlerts;
    }

    public ConcurrentHashMap<Long, AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public AtomicLong getEmployeeIdCounter() {
        return employeeIdCounter;
    }

    public AtomicLong getVisitorIdCounter() {
        return visitorIdCounter;
    }

    public AtomicLong getAppointmentIdCounter() {
        return appointmentIdCounter;
    }

    public AtomicLong getPassIdCounter() {
        return passIdCounter;
    }

    public AtomicLong getParkingSpotIdCounter() {
        return parkingSpotIdCounter;
    }

    public AtomicLong getMeetingRoomIdCounter() {
        return meetingRoomIdCounter;
    }

    public AtomicLong getPermissionIdCounter() {
        return permissionIdCounter;
    }

    public AtomicLong getAlertIdCounter() {
        return alertIdCounter;
    }

    public AtomicLong getLogIdCounter() {
        return logIdCounter;
    }
}
