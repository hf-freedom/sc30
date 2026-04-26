package com.campus.visitor.service;

import com.campus.visitor.entity.MeetingRoom;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingRoomService {
    
    @Autowired
    private InMemoryStorage storage;

    public List<MeetingRoom> getAllMeetingRooms() {
        return new ArrayList<>(storage.getMeetingRooms().values());
    }

    public List<MeetingRoom> getAvailableMeetingRooms() {
        List<MeetingRoom> result = new ArrayList<>();
        for (MeetingRoom room : storage.getMeetingRooms().values()) {
            if (room.getStatus() == MeetingRoom.MeetingRoomStatus.AVAILABLE) {
                result.add(room);
            }
        }
        return result;
    }

    public MeetingRoom getMeetingRoomById(Long id) {
        return storage.getMeetingRooms().get(id);
    }

    public MeetingRoom getMeetingRoomByAppointmentId(Long appointmentId) {
        for (MeetingRoom room : storage.getMeetingRooms().values()) {
            if (appointmentId.equals(room.getCurrentAppointmentId())) {
                return room;
            }
        }
        return null;
    }

    public boolean isAvailable(Long roomId) {
        MeetingRoom room = storage.getMeetingRooms().get(roomId);
        return room != null && room.getStatus() == MeetingRoom.MeetingRoomStatus.AVAILABLE;
    }

    public boolean occupyMeetingRoom(Long roomId, Long appointmentId) {
        MeetingRoom room = storage.getMeetingRooms().get(roomId);
        if (room == null) {
            return false;
        }
        if (room.getStatus() != MeetingRoom.MeetingRoomStatus.AVAILABLE) {
            return false;
        }
        room.setStatus(MeetingRoom.MeetingRoomStatus.OCCUPIED);
        room.setCurrentAppointmentId(appointmentId);
        storage.getMeetingRooms().put(roomId, room);
        return true;
    }

    public void releaseMeetingRoom(Long roomId, Long appointmentId) {
        MeetingRoom room = storage.getMeetingRooms().get(roomId);
        if (room != null && appointmentId.equals(room.getCurrentAppointmentId())) {
            room.setStatus(MeetingRoom.MeetingRoomStatus.AVAILABLE);
            room.setCurrentAppointmentId(null);
            storage.getMeetingRooms().put(roomId, room);
        }
    }

    public MeetingRoom createMeetingRoom(MeetingRoom meetingRoom) {
        long id = storage.getMeetingRoomIdCounter().getAndIncrement();
        meetingRoom.setId(id);
        storage.getMeetingRooms().put(id, meetingRoom);
        return meetingRoom;
    }
}
