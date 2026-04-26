package com.campus.visitor.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Visitor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String phoneNumber;
    private String idCardNumber;
    private String vehicleInfo;
    private Boolean isBlacklisted;
}
