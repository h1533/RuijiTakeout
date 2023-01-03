package com.jh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jh.reggie.commons.R;
import com.jh.reggie.model.dto.DishDto;
import com.jh.reggie.model.entity.Category;
import com.jh.reggie.model.entity.Dish;
import com.jh.reggie.model.entity.DishFlavor;
import com.jh.reggie.service.CategoryService;
import com.jh.reggie.service.DishFlavorService;
import com.jh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author JH
 * @description 菜品管理
 * @date 2022-12-07 20:28:23
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 默认显示未删除分类
        queryWrapper.eq(Dish::getIsDeleted, 0);
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        // 排序条件,默认降序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行Sql
        dishService.page(pageInfo, queryWrapper);

        // 拷贝对象
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 添加菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("添加菜品成功");
    }

    /**
     * 获取菜品和口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息和口味信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> edit(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    /**
     * 更新菜品上架状态
     *
     * @param status
     * @param ids
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, Long[] ids) {
        dishService.updateDishStatus(status, ids);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("状态修改成功");
    }

    /**
     * 删除菜品和口味信息
     *
     * @param ids
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.deleteDish(ids);
        return R.success("删除成功");
    }

    /**
     * 根据当前分类Id查询所有菜品
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    /*  public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 根据分类Id查询菜品
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 查询在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        // 排序方式
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
      }*/
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;
        // 动态设置Key，每一个分类都不一样
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        // 先从Redis中获取缓存数据，如果存在直接返回
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null) {
            return R.success(dishDtoList);
        }
        // 不存在则查询数据保存到Redis缓存中
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 根据分类Id查询菜品
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 查询在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        // 排序方式
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        dishDtoList = dishList.stream().map((item) -> {
            // 拷贝原始数据
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 根据分类ID查询菜品名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            // 分类不为空设置分类名称
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            // 获取菜品Id
            Long dishId = item.getId();
            // Sql: select * from dish_flavor where dish_id = ?
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            // 将查询到的DishFlavor数据保存到DTO中
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        // 如果不存在，需要设置Redis缓存数据
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}
