package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.commons.ErrorCode;
import com.jh.reggie.exception.CustomException;
import com.jh.reggie.model.dto.SetmealDto;
import com.jh.reggie.model.entity.Setmeal;
import com.jh.reggie.mappers.SetmealMapper;
import com.jh.reggie.model.entity.SetmealDish;
import com.jh.reggie.service.SetmealDishService;
import com.jh.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-07 20:26:07
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithSetmeal(SetmealDto setmealDto) {
        // 提交套餐默认字段
        this.save(setmealDto);
        // 批量提交setmeal_dishe字段
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 查询套餐包含菜品的信息
     *
     * @param id
     */
    @Override
    public SetmealDto getByIdSetmelWithDish(Long id) {
        // 根据Id查询套餐信息
        Setmeal setmeal = this.getById(id);
        // 查询套餐内菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        // 将信息拷贝到Dto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSetmeal(SetmealDto setmealDto) {
        // 提交默认数据
        this.updateById(setmealDto);
        // 删除原有的套餐内菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 新增当前套餐内菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            // 设置 套餐ID
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 修改套餐状态
     *
     * @param status
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(int status, Long[] ids) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId, ids).set(Setmeal::getStatus, status);
        this.update(updateWrapper);
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSetmeal(@RequestParam List<Long> ids) {
        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        long count = this.count(setmealQueryWrapper);
        if (count > 0) {
            throw new CustomException(ErrorCode.SETMEAL_STATUS_ERROR.getMessage());
        }
        // 删除套餐
        this.removeByIds(ids);
        // 删除菜品
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishQueryWrapper);
    }
}
