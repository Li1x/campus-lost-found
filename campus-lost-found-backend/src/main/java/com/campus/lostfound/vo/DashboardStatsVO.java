package com.campus.lostfound.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 管理员统计大屏数据 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO {

    /** 累计寻物启事总数 */
    private Long totalLostCount;

    /** 累计招领信息总数 */
    private Long totalFoundCount;

    /** 成功认领结案总数 */
    private Long totalClaimSuccessCount;

    /** 今日新增发布数 */
    private Long todayNewCount;

    /** 待管理员审核事项总数 */
    private Long pendingReviewCount;

    /** 各分类物品分布统计 */
    private List<Map<String, Object>> categoryDistribution;
}
