package com.campus.visitor.service;

import com.campus.visitor.entity.AccessPermission;
import com.campus.visitor.entity.Appointment;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccessPermissionService {
    
    @Autowired
    private InMemoryStorage storage;

    public List<AccessPermission> getAllPermissions() {
        return new ArrayList<>(storage.getAccessPermissions().values());
    }

    public AccessPermission getPermissionById(Long id) {
        return storage.getAccessPermissions().get(id);
    }

    public List<AccessPermission> getPermissionsByAppointmentId(Long appointmentId) {
        List<AccessPermission> result = new ArrayList<>();
        for (AccessPermission perm : storage.getAccessPermissions().values()) {
            if (appointmentId.equals(perm.getAppointmentId())) {
                result.add(perm);
            }
        }
        return result;
    }

    public AccessPermission createPermission(Appointment appointment) {
        List<AccessPermission> existing = getPermissionsByAppointmentId(appointment.getId());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        AccessPermission permission = new AccessPermission();
        permission.setId(storage.getPermissionIdCounter().getAndIncrement());
        permission.setAppointmentId(appointment.getId());
        permission.setAccessArea(appointment.getAccessArea());
        permission.setStartTime(appointment.getStartTime());
        permission.setEndTime(appointment.getEndTime());
        permission.setIsActive(true);

        storage.getAccessPermissions().put(permission.getId(), permission);
        return permission;
    }

    public void deactivatePermissions(Long appointmentId) {
        List<AccessPermission> permissions = getPermissionsByAppointmentId(appointmentId);
        for (AccessPermission perm : permissions) {
            perm.setIsActive(false);
            storage.getAccessPermissions().put(perm.getId(), perm);
        }
    }

    public boolean validateAccess(Long appointmentId, String accessArea, LocalDateTime accessTime) {
        List<AccessPermission> permissions = getPermissionsByAppointmentId(appointmentId);
        for (AccessPermission perm : permissions) {
            if (!perm.getIsActive()) {
                continue;
            }
            if (perm.getAccessArea() != null && !perm.getAccessArea().contains(accessArea)) {
                continue;
            }
            if (accessTime.isBefore(perm.getStartTime())) {
                continue;
            }
            if (accessTime.isAfter(perm.getEndTime())) {
                continue;
            }
            return true;
        }
        return false;
    }

    public boolean canAccessNow(Long appointmentId, String accessArea) {
        return validateAccess(appointmentId, accessArea, LocalDateTime.now());
    }
}
