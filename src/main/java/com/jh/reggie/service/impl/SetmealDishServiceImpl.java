package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.mappers.SetmealDishMapper;
import com.jh.reggie.model.entity.SetmealDish;
import com.jh.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

/**
* @author JH
* @description 针对表【setmeal_dish(套餐菜品关系)】的数据库操作Service实现
* @createDate 2022-12-14 21:21:58
*/
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish>
    implements SetmealDishService {
}




