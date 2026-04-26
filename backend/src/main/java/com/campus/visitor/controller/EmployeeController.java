package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.Employee;
import com.campus.visitor.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ApiResponse<List<Employee>> getAllEmployees() {
        return ApiResponse.success(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ApiResponse<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ApiResponse.error("员工不存在", 404);
        }
        return ApiResponse.success(employee);
    }

    @PostMapping
    public ApiResponse<Employee> createEmployee(@RequestBody Employee employee) {
        Employee created = employeeService.createEmployee(employee);
        return ApiResponse.success("员工创建成功", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updated = employeeService.updateEmployee(id, employee);
        if (updated == null) {
            return ApiResponse.error("员工不存在", 404);
        }
        return ApiResponse.success("员工更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeService.deleteEmployee(id);
        if (!deleted) {
            return ApiResponse.error("员工不存在", 404);
        }
        return ApiResponse.success("员工删除成功", null);
    }
}
