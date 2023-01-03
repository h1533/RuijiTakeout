package com.jh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jh.reggie.commons.BaseContext;
import com.jh.reggie.commons.R;
import com.jh.reggie.model.entity.Dish;
import com.jh.reggie.model.entity.ShoppingCart;
import com.jh.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author JH
 * @description 购物车
 * @date 2022-12-23 19:58:14
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("ShoppingCart:{}", shoppingCart);
        // 获取当前用户ID
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 查看套餐或菜品是否存在
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 如果菜品ID不存在，查询菜品。存在查询套餐
        if (dishId != null) {
            // 查询菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 查询套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        // 如果购物车中的套餐或菜品已存在则加一
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /**
     * 查询购物车数据
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        // 获取用户ID
        Long currentId = BaseContext.getCurrentId();

        // 查询条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        // SQL: select * from shopping_cart where user_id = ?
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCarts);
    }

    /**
     * 减菜品或套餐的数量
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("id: {}", shoppingCart);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        // 如果菜品Id!=null，添加查询条件
        if (shoppingCart.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        // 查询SQL: select * from shopping_cart where dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        // 如果购物车数据存在，库存-1
        if (cartServiceOne.getNumber() > 1) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            shoppingCartService.removeById(cartServiceOne);
        }
        return R.success(cartServiceOne);
    }

    /**
     * 清空购物车数据
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        // Sql: delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("购物车已清空");
    }
}
