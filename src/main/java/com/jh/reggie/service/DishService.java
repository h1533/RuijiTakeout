package com.jh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.reggie.model.dto.DishDto;
import com.jh.reggie.model.entity.Dish;

import java.util.List;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-07 20:24:57
 */
public interface DishService extends IService<Dish> {
    /**
     * 新增菜品
     *
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和口味信息
     *
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息和口味信息
     *
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 更新菜品上架状态
     *
     * @param status
     * @param ids
     */
    void updateDishStatus(int status, Long[] ids);

    /**
     * 删除菜品和口味信息
     *
     * @param ids
     */
    void deleteDish(List<Long> ids);
}
