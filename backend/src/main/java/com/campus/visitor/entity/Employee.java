package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String department;
    private Boolean canApproveVisitor;
    private EmployeeStatus status;
    
    public enum EmployeeStatus {
        ACTIVE,
        ON_LEAVE,
        RESIGNED
    }
}
