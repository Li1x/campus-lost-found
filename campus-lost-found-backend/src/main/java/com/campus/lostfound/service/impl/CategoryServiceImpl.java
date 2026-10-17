package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.entity.Category;
import com.campus.lostfound.mapper.CategoryMapper;
import com.campus.lostfound.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物品分类服务实现
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listActiveCategories() {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));
    }
}
