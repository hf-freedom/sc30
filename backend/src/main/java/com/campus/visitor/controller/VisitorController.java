package com.campus.visitor.controller;

import com.campus.visitor.dto.ApiResponse;
import com.campus.visitor.entity.Visitor;
import com.campus.visitor.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitors")
@CrossOrigin(origins = "*")
public class VisitorController {
    
    @Autowired
    private VisitorService visitorService;

    @GetMapping
    public ApiResponse<List<Visitor>> getAllVisitors() {
        return ApiResponse.success(visitorService.getAllVisitors());
    }

    @GetMapping("/{id}")
    public ApiResponse<Visitor> getVisitorById(@PathVariable Long id) {
        Visitor visitor = visitorService.getVisitorById(id);
        if (visitor == null) {
            return ApiResponse.error("访客不存在", 404);
        }
        return ApiResponse.success(visitor);
    }

    @PostMapping
    public ApiResponse<Visitor> createVisitor(@RequestBody Visitor visitor) {
        Visitor created = visitorService.createVisitor(visitor);
        return ApiResponse.success("访客创建成功", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Visitor> updateVisitor(@PathVariable Long id, @RequestBody Visitor visitor) {
        Visitor updated = visitorService.updateVisitor(id, visitor);
        if (updated == null) {
            return ApiResponse.error("访客不存在", 404);
        }
        return ApiResponse.success("访客更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteVisitor(@PathVariable Long id) {
        boolean deleted = visitorService.deleteVisitor(id);
        if (!deleted) {
            return ApiResponse.error("访客不存在", 404);
        }
        return ApiResponse.success("访客删除成功", null);
    }

    @PostMapping("/{id}/blacklist")
    public ApiResponse<Void> addToBlacklist(@PathVariable Long id, @RequestBody(required = false) String reason) {
        if (reason == null) {
            reason = "违规行为";
        }
        visitorService.addToBlacklist(id, reason);
        return ApiResponse.success("已加入黑名单", null);
    }

    @DeleteMapping("/{id}/blacklist")
    public ApiResponse<Void> removeFromBlacklist(@PathVariable Long id) {
        visitorService.removeFromBlacklist(id);
        return ApiResponse.success("已移出黑名单", null);
    }
}
