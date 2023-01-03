package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.exception.CustomException;
import com.jh.reggie.commons.ErrorCode;
import com.jh.reggie.model.entity.Category;
import com.jh.reggie.model.entity.Dish;
import com.jh.reggie.mappers.CategoryMapper;
import com.jh.reggie.model.entity.Setmeal;
import com.jh.reggie.service.CategoryService;
import com.jh.reggie.service.DishService;
import com.jh.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-07 18:45:03
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 删除分类
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 查询该分类下是否绑定菜品,如果绑定了,抛出异常
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        long dishCount = dishService.count(dishQueryWrapper);
        if (dishCount > 0) {
            throw new CustomException(ErrorCode.ASSOCIATED_DISH.getMessage());
        }
        // 查询该分类下是否绑定套餐,如果绑定了,抛出异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        long setmealCount = setmealService.count(setmealQueryWrapper);
        if (setmealCount > 0) {
            throw new CustomException(ErrorCode.ASSOCIATED_SETMEAL.getMessage());
        }
        // 删除分类
        this.removeById(id);
    }
}
