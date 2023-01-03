package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.mappers.ShoppingCartMapper;
import com.jh.reggie.model.entity.ShoppingCart;
import com.jh.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
* @author JH
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-12-21 16:22:37
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

}




