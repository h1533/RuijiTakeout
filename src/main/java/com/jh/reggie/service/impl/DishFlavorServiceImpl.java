package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.mappers.DishFlavorMapper;
import com.jh.reggie.model.entity.DishFlavor;
import com.jh.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-12 12:46:21
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
