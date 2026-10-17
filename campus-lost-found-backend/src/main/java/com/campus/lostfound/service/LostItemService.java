package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lostfound.dto.LostItemCreateRequest;
import com.campus.lostfound.entity.LostItem;
import com.campus.lostfound.vo.LostItemVO;

/**
 * 寻物启事服务接口
 */
public interface LostItemService extends IService<LostItem> {

    /**
     * 创建寻物启事（送审）
     */
    LostItemVO createLostItem(Long userId, LostItemCreateRequest request);

    /**
     * 公开分页列表（只展示已审核通过 APPROVED）
     */
    Page<LostItemVO> pagePublicLostItems(Integer categoryId, String keyword, int page, int size);

    /**
     * 分页查询当前用户发布的寻物启事
     */
    Page<LostItemVO> pageMyLostItems(Long userId, int page, int size);

    /**
     * 获取详情（带联系方式脱敏与图片列表）
     */
    LostItemVO getLostItemDetail(Long id, Long currentUserId);

    /**
     * 关闭寻物启事（结案）
     */
    void closeLostItem(Long id, Long currentUserId);
}
