package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lostfound.dto.ClaimCreateRequest;
import com.campus.lostfound.dto.OwnerAuditRequest;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.vo.ClaimVO;

/**
 * 认领申请服务接口
 */
public interface ClaimService extends IService<Claim> {

    /**
     * 发起认领申请
     */
    ClaimVO createClaim(Long claimantId, ClaimCreateRequest request);

    /**
     * 分页查询当前用户发起的认领记录
     */
    Page<ClaimVO> pageMyClaims(Long claimantId, int page, int size);

    /**
     * 分页查询当前用户收到的认领申请
     */
    Page<ClaimVO> pageReceivedClaims(Long ownerId, int page, int size);

    /**
     * 发布者核验认领申请（第一道审核）
     */
    void ownerAudit(Long claimId, Long ownerId, OwnerAuditRequest request);

    /**
     * 申请人主动撤销认领
     */
    void cancelClaim(Long claimId, Long claimantId);
}
