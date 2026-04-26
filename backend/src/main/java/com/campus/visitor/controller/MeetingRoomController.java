package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.MeetingRoom;
import com.campus.visitor.service.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meeting-rooms")
@CrossOrigin(origins = "*")
public class MeetingRoomController {
    
    @Autowired
    private MeetingRoomService meetingRoomService;

    @GetMapping
    public ApiResponse<List<MeetingRoom>> getAllMeetingRooms() {
        return ApiResponse.success(meetingRoomService.getAllMeetingRooms());
    }

    @GetMapping("/available")
    public ApiResponse<List<MeetingRoom>> getAvailableMeetingRooms() {
        return ApiResponse.success(meetingRoomService.getAvailableMeetingRooms());
    }

    @GetMapping("/{id}")
    public ApiResponse<MeetingRoom> getMeetingRoomById(@PathVariable Long id) {
        MeetingRoom room = meetingRoomService.getMeetingRoomById(id);
        if (room == null) {
            return ApiResponse.error("会议室不存在", 404);
        }
        return ApiResponse.success(room);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ApiResponse<MeetingRoom> getMeetingRoomByAppointmentId(@PathVariable Long appointmentId) {
        MeetingRoom room = meetingRoomService.getMeetingRoomByAppointmentId(appointmentId);
        if (room == null) {
            return ApiResponse.error("未找到对应会议室", 404);
        }
        return ApiResponse.success(room);
    }

    @PostMapping
    public ApiResponse<MeetingRoom> createMeetingRoom(@RequestBody MeetingRoom meetingRoom) {
        MeetingRoom created = meetingRoomService.createMeetingRoom(meetingRoom);
        return ApiResponse.success("会议室创建成功", created);
    }

    @PostMapping("/{id}/release")
    public ApiResponse<Void> releaseMeetingRoom(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Long> body) {
        Long appointmentId = body != null && body.containsKey("appointmentId") ? 
            body.get("appointmentId") : null;
        meetingRoomService.releaseMeetingRoom(id, appointmentId);
        return ApiResponse.success("会议室已释放", null);
    }

    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getMeetingRoomStatus() {
        List<MeetingRoom> all = meetingRoomService.getAllMeetingRooms();
        List<MeetingRoom> available = meetingRoomService.getAvailableMeetingRooms();
        
        Map<String, Object> status = new HashMap<>();
        status.put("total", all.size());
        status.put("available", available.size());
        status.put("occupied", all.size() - available.size());
        
        return ApiResponse.success(status);
    }
}
