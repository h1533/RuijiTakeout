package com.jh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.reggie.model.dto.SetmealDto;
import com.jh.reggie.model.entity.Setmeal;

import java.util.List;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-07 20:25:28
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐
     *
     * @param setmealDto
     */
    void saveWithSetmeal(SetmealDto setmealDto);

    /**
     * 查询套餐包含菜品的信息
     *
     * @param id
     * @return
     */
    SetmealDto getByIdSetmelWithDish(Long id);

    /**
     * 修改套餐
     *
     * @param setmealDto
     */
    void updateSetmeal(SetmealDto setmealDto);

    /**
     * 更新套餐状态
     *
     * @param status
     * @param ids
     */
    void updateStatus(int status, Long[] ids);

    /**
     * 删除套餐
     *
     * @param ids
     */
    void deleteSetmeal(List<Long> ids);
}
