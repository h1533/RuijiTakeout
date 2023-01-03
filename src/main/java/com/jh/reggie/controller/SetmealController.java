package com.jh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jh.reggie.commons.ErrorCode;
import com.jh.reggie.commons.R;
import com.jh.reggie.exception.CustomException;
import com.jh.reggie.model.dto.DishDto;
import com.jh.reggie.model.dto.SetmealDishDto;
import com.jh.reggie.model.dto.SetmealDto;
import com.jh.reggie.model.entity.*;
import com.jh.reggie.service.CategoryService;
import com.jh.reggie.service.DishService;
import com.jh.reggie.service.SetmealDishService;
import com.jh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author JH
 * @description 套餐管理
 * @date 2022-12-07 20:33:59
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")

    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 默认显示未删除套餐
        queryWrapper.eq(Setmeal::getIsDeleted, 0);
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        // 排序条件，默认降序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        // 执行Sql
        setmealService.page(pageInfo, queryWrapper);

        // 将 pageInfo 默认信息拷贝到 setmealDtoPage 过滤掉 records 数据
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        // 获得 records
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            // 获取分类ID
            Long categoryId = item.getCategoryId();
            // 获取套餐分类名
            Category category = categoryService.getById(categoryId);
            String setmealName = category.getName();
            // 设置套餐分类名
            setmealDto.setCategoryName(setmealName);
            return setmealDto;
        }).collect(Collectors.toList());
        // 填充数据列表
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithSetmeal(setmealDto);
        String key = "setmeal_" + setmealDto.getCategoryId() + "_" + setmealDto.getStatus();
        redisTemplate.delete(key);
        return R.success("新增套餐成功");
    }

    /**
     * 根据id查询菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdSetmelWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmeal(setmealDto);
        String key = "setmeal_" + setmealDto.getCategoryId() + "_" + setmealDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改套餐成功");
    }

    /**
     * 更新套餐状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, Long[] ids) {
        setmealService.updateStatus(status, ids);
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);
        return R.success("修改套餐状态成功");
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteSetmeal(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 根据分类ID查询套餐
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> setmealList = null;
        // 动态设置Key
        String key = "setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();
        // 先查询Redis中是否存在缓存数据
        setmealList = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        // 已存在直接返回
        if (setmealList != null) {
            return R.success(setmealList);
        }

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        // 根据分类Id查询套餐
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        // 查询在售状态的菜品
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, 1);
        // 排序方式
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealList = setmealService.list(wrapper);

        // 如果缓存数据不存在，则保存数据
        redisTemplate.opsForValue().set(key, setmealList, 60, TimeUnit.MINUTES);
        return R.success(setmealList);
    }

    /**
     * 获取套餐菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDishDto>> getSetmeal(@PathVariable Long id) {
        // 查询套餐内菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        // 根据 套餐ID 循环查询菜品ID并保存图片名称
        List<SetmealDishDto> list = setmealDishList.stream().map((item) -> {
            SetmealDishDto setmealDishDto = new SetmealDishDto();
            BeanUtils.copyProperties(item, setmealDishDto);
            // 获取菜品ID
            Long dishId = item.getDishId();
            // 查询菜品信息获取图片信息
            Dish dish = dishService.getById(dishId);
            String image = dish.getImage();
            // 将图片信息设置到Dto中
            setmealDishDto.setImage(image);
            return setmealDishDto;
        }).collect(Collectors.toList());
        return R.success(list);
    }
}
