package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.dto.ClaimCreateRequest;
import com.campus.lostfound.dto.OwnerAuditRequest;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.ClaimService;
import com.campus.lostfound.vo.ClaimVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认领申请控制器
 */
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    /**
     * 发起认领申请
     */
    @PostMapping
    public Result<ClaimVO> create(@Valid @RequestBody ClaimCreateRequest request) {
        Long claimantId = UserContext.getCurrentUserId();
        ClaimVO claim = claimService.createClaim(claimantId, request);
        return Result.ok("认领申请已提交，等待发布者核对", claim);
    }

    /**
     * 分页查询我发起的认领申请
     */
    @GetMapping("/my")
    public Result<Page<ClaimVO>> pageMyClaims(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long claimantId = UserContext.getCurrentUserId();
        Page<ClaimVO> pageResult = claimService.pageMyClaims(claimantId, page, size);
        return Result.ok(pageResult);
    }

    /**
     * 分页查询我收到的认领申请
     */
    @GetMapping("/received")
    public Result<Page<ClaimVO>> pageReceivedClaims(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long ownerId = UserContext.getCurrentUserId();
        Page<ClaimVO> pageResult = claimService.pageReceivedClaims(ownerId, page, size);
        return Result.ok(pageResult);
    }

    /**
     * 发布者核验初审
     */
    @PutMapping("/{id}/owner-audit")
    public Result<Void> ownerAudit(
            @PathVariable("id") Long claimId,
            @Valid @RequestBody OwnerAuditRequest request
    ) {
        Long ownerId = UserContext.getCurrentUserId();
        claimService.ownerAudit(claimId, ownerId, request);
        return Result.ok("操作成功");
    }

    /**
     * 申请人撤销认领申请
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable("id") Long claimId) {
        Long claimantId = UserContext.getCurrentUserId();
        claimService.cancelClaim(claimId, claimantId);
        return Result.ok("认领申请已撤销");
    }
}
