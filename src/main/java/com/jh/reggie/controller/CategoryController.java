package com.jh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jh.reggie.commons.R;
import com.jh.reggie.model.entity.Category;
import com.jh.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author JH
 * @description 分类和套餐
 * @date 2022-12-07 18:49:14
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

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
    public R<Page> list(int page, int pageSize) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加分页条件
        queryWrapper.orderByDesc(Category::getSort);
        // 执行分页查询
        Page<Category> categorys = categoryService.page(new Page<>(page, pageSize), queryWrapper);
        return R.success(categorys);
    }

    /**
     * 新增菜品或套餐
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        redisTemplate.delete("category");
        return R.success("添加成功");
    }

    /**
     * 删除菜品或套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        categoryService.remove(ids);
        redisTemplate.delete("category");
        return R.success("删除成功");
    }

    /**
     * 根据id修改菜品或套餐分类
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        redisTemplate.delete("category");
        return R.success("修改成功");
    }

    /**
     * 根据分类类型查询
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        List<Category> categories = null;
        categories = (List<Category>) redisTemplate.opsForValue().get("category");
        if (categories != null) {
            return R.success(categories);
        }
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        categories = categoryService.list(queryWrapper);

        redisTemplate.opsForValue().set("category", categories, 60, TimeUnit.MINUTES);
        return R.success(categories);
    }
}
