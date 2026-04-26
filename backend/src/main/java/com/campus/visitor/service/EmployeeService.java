package com.campus.visitor.service;

import com.campus.visitor.entity.Employee;
import com.campus.visitor.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    
    @Autowired
    private InMemoryStorage storage;

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(storage.getEmployees().values());
    }

    public Employee getEmployeeById(Long id) {
        return storage.getEmployees().get(id);
    }

    public Employee createEmployee(Employee employee) {
        long id = storage.getEmployeeIdCounter().getAndIncrement();
        employee.setId(id);
        storage.getEmployees().put(id, employee);
        return employee;
    }

    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = storage.getEmployees().get(id);
        if (existing == null) {
            return null;
        }
        employee.setId(id);
        storage.getEmployees().put(id, employee);
        return employee;
    }

    public boolean deleteEmployee(Long id) {
        return storage.getEmployees().remove(id) != null;
    }

    public boolean canApproveVisitor(Long employeeId) {
        Employee employee = storage.getEmployees().get(employeeId);
        return employee != null && employee.getCanApproveVisitor() != null && employee.getCanApproveVisitor();
    }

    public boolean isActive(Long employeeId) {
        Employee employee = storage.getEmployees().get(employeeId);
        return employee != null && employee.getStatus() == Employee.EmployeeStatus.ACTIVE;
    }
}
