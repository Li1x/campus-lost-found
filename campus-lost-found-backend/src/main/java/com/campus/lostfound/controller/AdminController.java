package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.common.enums.UserRole;
import com.campus.lostfound.dto.AdminAuditRequest;
import com.campus.lostfound.dto.UserStatusRequest;
import com.campus.lostfound.security.RequireRole;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.AdminService;
import com.campus.lostfound.vo.ClaimVO;
import com.campus.lostfound.vo.DashboardStatsVO;
import com.campus.lostfound.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员后台控制器
 * 严格限制仅 ADMIN 和 SUPER_ADMIN 角色可访问
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@RequireRole({UserRole.ADMIN, UserRole.SUPER_ADMIN})
public class AdminController {

    private final AdminService adminService;

    /**
     * 待审核物品发布列表
     */
    @GetMapping("/reviews/items")
    public Result<Page<Map<String, Object>>> pagePendingItems(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Map<String, Object>> pageResult = adminService.pagePendingItems(page, size);
        return Result.ok(pageResult);
    }

    /**
     * 审核物品发布
     */
    @PutMapping("/reviews/items/{itemType}/{id}")
    public Result<Void> auditItem(
            @PathVariable("itemType") String itemType,
            @PathVariable("id") Long id,
            @Valid @RequestBody AdminAuditRequest request
    ) {
        Long reviewerId = UserContext.getCurrentUserId();
        adminService.auditItem(itemType, id, reviewerId, request);
        return Result.ok("审核处理完成");
    }

    /**
     * 待终审认领列表
     */
    @GetMapping("/reviews/claims")
    public Result<Page<ClaimVO>> pagePendingClaims(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<ClaimVO> pageResult = adminService.pagePendingClaims(page, size);
        return Result.ok(pageResult);
    }

    /**
     * 终审认领申请
     */
    @PutMapping("/reviews/claims/{id}")
    public Result<Void> auditClaim(
            @PathVariable("id") Long claimId,
            @Valid @RequestBody AdminAuditRequest request
    ) {
        Long reviewerId = UserContext.getCurrentUserId();
        adminService.auditClaim(claimId, reviewerId, request);
        return Result.ok("认领终审处理完成");
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    public Result<Page<UserInfoVO>> pageUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<UserInfoVO> pageResult = adminService.pageUsers(page, size);
        return Result.ok(pageResult);
    }

    /**
     * 启用/禁用用户账号
     */
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable("id") Long targetUserId,
            @Valid @RequestBody UserStatusRequest request
    ) {
        Long operatorId = UserContext.getCurrentUserId();
        adminService.updateUserStatus(targetUserId, operatorId, request);
        return Result.ok("账号状态已更新");
    }

    /**
     * 重置用户密码为初始密码 123456
     */
    @PutMapping("/users/{id}/reset-password")
    public Result<Void> resetUserPassword(@PathVariable("id") Long targetUserId) {
        Long operatorId = UserContext.getCurrentUserId();
        adminService.resetUserPassword(targetUserId, operatorId);
        return Result.ok("密码已重置为初始密码 123456");
    }

    /**
     * 控制台统计大屏数据
     */
    @GetMapping("/statistics/dashboard")
    public Result<DashboardStatsVO> getDashboardStatistics() {
        DashboardStatsVO stats = adminService.getDashboardStatistics();
        return Result.ok(stats);
    }
}
