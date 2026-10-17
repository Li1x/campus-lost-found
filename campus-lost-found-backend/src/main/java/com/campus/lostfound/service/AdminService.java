package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.dto.AdminAuditRequest;
import com.campus.lostfound.dto.UserStatusRequest;
import com.campus.lostfound.vo.ClaimVO;
import com.campus.lostfound.vo.DashboardStatsVO;
import com.campus.lostfound.vo.UserInfoVO;

import java.util.Map;

/**
 * 管理员业务服务接口
 */
public interface AdminService {

    /**
     * 查询所有待审核物品列表（包含失物与招领）
     */
    Page<Map<String, Object>> pagePendingItems(int page, int size);

    /**
     * 审核物品发布申请
     */
    void auditItem(String itemType, Long id, Long reviewerId, AdminAuditRequest request);

    /**
     * 查询所有待终审的认领记录（状态为 OWNER_APPROVED）
     */
    Page<ClaimVO> pagePendingClaims(int page, int size);

    /**
     * 认领终审
     */
    void auditClaim(Long claimId, Long reviewerId, AdminAuditRequest request);

    /**
     * 分页查询用户列表
     */
    Page<UserInfoVO> pageUsers(int page, int size);

    /**
     * 修改用户账号状态（禁用/解禁）
     */
    void updateUserStatus(Long targetUserId, Long operatorId, UserStatusRequest request);

    /**
     * 重置用户密码为初始密码
     */
    void resetUserPassword(Long targetUserId, Long operatorId);

    /**
     * 获取管理控制台统计大屏数据
     */
    DashboardStatsVO getDashboardStatistics();
}
