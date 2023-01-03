package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.commons.ErrorCode;
import com.jh.reggie.exception.CustomException;
import com.jh.reggie.model.dto.DishDto;
import com.jh.reggie.model.entity.Dish;
import com.jh.reggie.mappers.DishMapper;
import com.jh.reggie.model.entity.DishFlavor;
import com.jh.reggie.service.DishFlavorService;
import com.jh.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-07 20:27:14
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应口味
     *
     * @param dishDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息
        this.save(dishDto);
        // 获取菜品ID
        Long dishId = dishDto.getId();
        // 重组Flavors集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        // 保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询Dish菜品信息
        Dish dish = this.getById(id);
        // 查询DishFlavor口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        // 将信息添加到DishDto中
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息和口味信息
     *
     * @param dishDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品基本信息
        this.updateById(dishDto);
        // 清理当前口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 添加当前提交口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 更新菜品上架状态
     *
     * @param status
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDishStatus(int status, Long[] ids) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, ids).set(Dish::getStatus, status);
        this.update(updateWrapper);
    }

    /**
     * 删除菜品和口味信息
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDish(List<Long> ids) {
        // 删除菜品信息
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(Dish::getId, ids).eq(Dish::getStatus, 1);
        long count = this.count(dishQueryWrapper);
        if (count > 0) {
            throw new CustomException(ErrorCode.DISH_STATUS_ERROR.getMessage());
        }
        this.removeByIds(ids);
        // 删除口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishFlavorQueryWrapper);
    }
}
