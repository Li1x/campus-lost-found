package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lostfound.entity.Category;

import java.util.List;

/**
 * 物品分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有启用的物品分类列表（按 sortOrder 升序排列）
     */
    List<Category> listActiveCategories();
}
