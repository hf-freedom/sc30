package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.ParkingSpot;
import com.campus.visitor.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "*")
public class ParkingController {
    
    @Autowired
    private ParkingService parkingService;

    @GetMapping("/spots")
    public ApiResponse<List<ParkingSpot>> getAllParkingSpots() {
        return ApiResponse.success(parkingService.getAllParkingSpots());
    }

    @GetMapping("/spots/available")
    public ApiResponse<List<ParkingSpot>> getAvailableParkingSpots() {
        return ApiResponse.success(parkingService.getAvailableParkingSpots());
    }

    @GetMapping("/spots/{id}")
    public ApiResponse<ParkingSpot> getParkingSpotById(@PathVariable Long id) {
        ParkingSpot spot = parkingService.getParkingSpotById(id);
        if (spot == null) {
            return ApiResponse.error("停车位不存在", 404);
        }
        return ApiResponse.success(spot);
    }

    @GetMapping("/spots/appointment/{appointmentId}")
    public ApiResponse<ParkingSpot> getParkingSpotByAppointmentId(@PathVariable Long appointmentId) {
        ParkingSpot spot = parkingService.getParkingSpotByAppointmentId(appointmentId);
        if (spot == null) {
            return ApiResponse.error("未找到对应停车位", 404);
        }
        return ApiResponse.success(spot);
    }

    @PostMapping("/spots")
    public ApiResponse<ParkingSpot> createParkingSpot(@RequestBody ParkingSpot parkingSpot) {
        ParkingSpot created = parkingService.createParkingSpot(parkingSpot);
        return ApiResponse.success("停车位创建成功", created);
    }

    @PostMapping("/release/{appointmentId}")
    public ApiResponse<Void> releaseParkingSpot(@PathVariable Long appointmentId) {
        parkingService.releaseParkingSpot(appointmentId);
        return ApiResponse.success("停车位已释放", null);
    }

    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getParkingStatus() {
        List<ParkingSpot> all = parkingService.getAllParkingSpots();
        List<ParkingSpot> available = parkingService.getAvailableParkingSpots();
        
        Map<String, Object> status = new HashMap<>();
        status.put("total", all.size());
        status.put("available", available.size());
        status.put("occupied", all.size() - available.size());
        
        return ApiResponse.success(status);
    }
}
