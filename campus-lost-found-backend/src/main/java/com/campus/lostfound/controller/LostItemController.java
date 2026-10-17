package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.dto.LostItemCreateRequest;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.LostItemService;
import com.campus.lostfound.vo.LostItemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 寻物启事控制器
 */
@RestController
@RequestMapping("/api/lost-items")
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;

    /**
     * 发布寻物启事
     */
    @PostMapping
    public Result<LostItemVO> create(@Valid @RequestBody LostItemCreateRequest request) {
        Long userId = UserContext.getCurrentUserId();
        LostItemVO item = lostItemService.createLostItem(userId, request);
        return Result.ok("发布成功，已提交管理员审核", item);
    }

    /**
     * 公开寻物启事分页列表
     */
    @GetMapping
    public Result<Page<LostItemVO>> pagePublic(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "12") int size
    ) {
        Page<LostItemVO> pageResult = lostItemService.pagePublicLostItems(categoryId, keyword, page, size);
        return Result.ok(pageResult);
    }

    /**
     * 查询我发布的寻物启事
     */
    @GetMapping("/my")
    public Result<Page<LostItemVO>> pageMy(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long userId = UserContext.getCurrentUserId();
        Page<LostItemVO> pageResult = lostItemService.pageMyLostItems(userId, page, size);
        return Result.ok(pageResult);
    }

    /**
     * 查询寻物启事详情
     */
    @GetMapping("/{id}")
    public Result<LostItemVO> getDetail(@PathVariable("id") Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        LostItemVO item = lostItemService.getLostItemDetail(id, currentUserId);
        return Result.ok(item);
    }

    /**
     * 关闭寻物启事
     */
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable("id") Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        lostItemService.closeLostItem(id, currentUserId);
        return Result.ok("寻物启事已关闭");
    }
}
