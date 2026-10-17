package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lostfound.dto.FoundItemCreateRequest;
import com.campus.lostfound.entity.FoundItem;
import com.campus.lostfound.vo.FoundItemVO;

/**
 * 招领信息服务接口
 */
public interface FoundItemService extends IService<FoundItem> {

    /**
     * 创建招领启事（送审）
     */
    FoundItemVO createFoundItem(Long userId, FoundItemCreateRequest request);

    /**
     * 公开分页列表（只展示已审核通过 APPROVED）
     */
    Page<FoundItemVO> pagePublicFoundItems(Integer categoryId, String keyword, int page, int size);

    /**
     * 分页查询当前用户发布的招领启事
     */
    Page<FoundItemVO> pageMyFoundItems(Long userId, int page, int size);

    /**
     * 获取详情（带联系方式脱敏与图片列表）
     */
    FoundItemVO getFoundItemDetail(Long id, Long currentUserId);

    /**
     * 关闭招领启事（结案）
     */
    void closeFoundItem(Long id, Long currentUserId);
}
