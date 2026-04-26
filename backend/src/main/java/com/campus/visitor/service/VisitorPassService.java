package com.campus.visitor.service;

import com.campus.visitor.entity.Appointment;
import com.campus.visitor.entity.VisitorPass;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VisitorPassService {
    
    @Autowired
    private InMemoryStorage storage;

    public List<VisitorPass> getAllPasses() {
        return new ArrayList<>(storage.getVisitorPasses().values());
    }

    public VisitorPass getPassById(Long id) {
        return storage.getVisitorPasses().get(id);
    }

    public VisitorPass getPassByAppointmentId(Long appointmentId) {
        for (VisitorPass pass : storage.getVisitorPasses().values()) {
            if (appointmentId.equals(pass.getAppointmentId())) {
                return pass;
            }
        }
        return null;
    }

    public VisitorPass getPassByCode(String passCode) {
        for (VisitorPass pass : storage.getVisitorPasses().values()) {
            if (passCode.equals(pass.getPassCode())) {
                return pass;
            }
        }
        return null;
    }

    public VisitorPass createPass(Appointment appointment) {
        VisitorPass existing = getPassByAppointmentId(appointment.getId());
        if (existing != null) {
            return existing;
        }

        VisitorPass pass = new VisitorPass();
        pass.setId(storage.getPassIdCounter().getAndIncrement());
        pass.setAppointmentId(appointment.getId());
        pass.setPassCode(generatePassCode());
        pass.setQrCode(generateQRCodeContent(pass.getPassCode()));
        pass.setIsActive(true);
        pass.setEffectiveTime(appointment.getStartTime());
        pass.setExpireTime(appointment.getEndTime());
        pass.setAccessAreas(appointment.getAccessArea());

        storage.getVisitorPasses().put(pass.getId(), pass);
        return pass;
    }

    public void deactivatePass(Long appointmentId) {
        VisitorPass pass = getPassByAppointmentId(appointmentId);
        if (pass != null) {
            pass.setIsActive(false);
            storage.getVisitorPasses().put(pass.getId(), pass);
        }
    }

    public boolean validatePass(String passCode, String accessArea) {
        VisitorPass pass = getPassByCode(passCode);
        if (pass == null) {
            return false;
        }
        if (!pass.getIsActive()) {
            return false;
        }
        if (pass.getAccessAreas() != null && !pass.getAccessAreas().contains(accessArea)) {
            return false;
        }
        return true;
    }

    private String generatePassCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private String generateQRCodeContent(String passCode) {
        return "campus://visitor/" + passCode;
    }
}
