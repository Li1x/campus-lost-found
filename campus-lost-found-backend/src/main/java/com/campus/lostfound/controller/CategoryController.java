package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.Category;
import com.campus.lostfound.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 物品分类公开接口
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取启用的分类列表
     */
    @GetMapping
    public Result<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.listActiveCategories();
        return Result.ok(categories);
    }
}
