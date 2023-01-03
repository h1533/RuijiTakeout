package com.jh.reggie.model.dto;

import com.jh.reggie.model.entity.Dish;
import com.jh.reggie.model.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JH
 * @description 数据传输对象
 * @date 2022-12-12 16:39:17
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
