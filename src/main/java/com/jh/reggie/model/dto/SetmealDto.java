package com.jh.reggie.model.dto;

import com.jh.reggie.model.entity.Setmeal;
import com.jh.reggie.model.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
