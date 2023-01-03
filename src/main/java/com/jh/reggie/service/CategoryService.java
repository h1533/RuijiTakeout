package com.jh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.reggie.model.entity.Category;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-07 18:45:03
 */
public interface CategoryService extends IService<Category> {

    /**
     * 删除分类
     *
     * @param id
     */
    void remove(Long id);
}
