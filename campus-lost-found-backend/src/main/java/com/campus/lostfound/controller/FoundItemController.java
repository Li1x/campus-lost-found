package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.dto.FoundItemCreateRequest;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.FoundItemService;
import com.campus.lostfound.vo.FoundItemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 招领信息控制器
 */
@RestController
@RequestMapping("/api/found-items")
@RequiredArgsConstructor
public class FoundItemController {

    private final FoundItemService foundItemService;

    /**
     * 发布招领启事
     */
    @PostMapping
    public Result<FoundItemVO> create(@Valid @RequestBody FoundItemCreateRequest request) {
        Long userId = UserContext.getCurrentUserId();
        FoundItemVO item = foundItemService.createFoundItem(userId, request);
        return Result.ok("发布成功，已提交管理员审核", item);
    }

    /**
     * 公开招领启事分页列表
     */
    @GetMapping
    public Result<Page<FoundItemVO>> pagePublic(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "12") int size
    ) {
        Page<FoundItemVO> pageResult = foundItemService.pagePublicFoundItems(categoryId, keyword, page, size);
        return Result.ok(pageResult);
    }

    /**
     * 查询我发布的招领启事
     */
    @GetMapping("/my")
    public Result<Page<FoundItemVO>> pageMy(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long userId = UserContext.getCurrentUserId();
        Page<FoundItemVO> pageResult = foundItemService.pageMyFoundItems(userId, page, size);
        return Result.ok(pageResult);
    }

    /**
     * 查询招领启事详情
     */
    @GetMapping("/{id}")
    public Result<FoundItemVO> getDetail(@PathVariable("id") Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        FoundItemVO item = foundItemService.getFoundItemDetail(id, currentUserId);
        return Result.ok(item);
    }

    /**
     * 关闭招领启事
     */
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable("id") Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        foundItemService.closeFoundItem(id, currentUserId);
        return Result.ok("招领启事已关闭");
    }
}
