package com.campus.visitor.service;

import com.campus.visitor.entity.Visitor;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisitorService {
    
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private AuditLogService auditLogService;

    public List<Visitor> getAllVisitors() {
        return new ArrayList<>(storage.getVisitors().values());
    }

    public Visitor getVisitorById(Long id) {
        return storage.getVisitors().get(id);
    }

    public Visitor getVisitorByIdCard(String idCardNumber) {
        for (Visitor visitor : storage.getVisitors().values()) {
            if (idCardNumber.equals(visitor.getIdCardNumber())) {
                return visitor;
            }
        }
        return null;
    }

    public Visitor getVisitorByPhone(String phoneNumber) {
        for (Visitor visitor : storage.getVisitors().values()) {
            if (phoneNumber.equals(visitor.getPhoneNumber())) {
                return visitor;
            }
        }
        return null;
    }

    public Visitor createVisitor(Visitor visitor) {
        Visitor existing = getVisitorByIdCard(visitor.getIdCardNumber());
        if (existing != null) {
            return existing;
        }
        long id = storage.getVisitorIdCounter().getAndIncrement();
        visitor.setId(id);
        if (visitor.getIsBlacklisted() == null) {
            visitor.setIsBlacklisted(false);
        }
        storage.getVisitors().put(id, visitor);
        
        auditLogService.createLog(null, "NEW_VISITOR", 
            "新访客登记: " + visitor.getName() + " (手机号: " + visitor.getPhoneNumber() + ")",
            visitor.getName(), visitor.getPhoneNumber(), null, "系统");
        
        return visitor;
    }

    public Visitor updateVisitor(Long id, Visitor visitor) {
        Visitor existing = storage.getVisitors().get(id);
        if (existing == null) {
            return null;
        }
        visitor.setId(id);
        storage.getVisitors().put(id, visitor);
        return visitor;
    }

    public boolean deleteVisitor(Long id) {
        return storage.getVisitors().remove(id) != null;
    }

    public boolean isBlacklisted(Long visitorId) {
        Visitor visitor = storage.getVisitors().get(visitorId);
        return visitor != null && visitor.getIsBlacklisted() != null && visitor.getIsBlacklisted();
    }

    public void addToBlacklist(Long visitorId, String reason) {
        Visitor visitor = storage.getVisitors().get(visitorId);
        if (visitor != null) {
            visitor.setIsBlacklisted(true);
            storage.getVisitors().put(visitorId, visitor);
            
            auditLogService.createLog(null, "BLACKLIST_ADD", 
                "访客加入黑名单: " + visitor.getName() + ", 原因: " + reason,
                visitor.getName(), visitor.getPhoneNumber(), null, "系统");
        }
    }

    public void removeFromBlacklist(Long visitorId) {
        Visitor visitor = storage.getVisitors().get(visitorId);
        if (visitor != null) {
            visitor.setIsBlacklisted(false);
            storage.getVisitors().put(visitorId, visitor);
            
            auditLogService.createLog(null, "BLACKLIST_REMOVE", 
                "访客移出黑名单: " + visitor.getName(),
                visitor.getName(), visitor.getPhoneNumber(), null, "系统");
        }
    }
}
