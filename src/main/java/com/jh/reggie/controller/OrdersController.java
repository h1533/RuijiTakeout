package com.jh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jh.reggie.commons.BaseContext;
import com.jh.reggie.commons.R;
import com.jh.reggie.model.dto.OrdersDto;
import com.jh.reggie.model.entity.OrderDetail;
import com.jh.reggie.model.entity.Orders;
import com.jh.reggie.service.OrderDetailService;
import com.jh.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JH
 * @description TODO
 * @date 2023-01-02 09:07:09
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(Integer page, Integer pageSize) {
        Page<Orders> pageInfo = new Page<>();
        Page<OrdersDto> ordersDtoPage = new Page<>();

        // 根据用户查询最新订单
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        wrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, wrapper);

        // 拷贝基础信息
        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        // 获取Records
        List<Orders> records = pageInfo.getRecords();

        // 循环查询菜品或套餐信息
        List<OrdersDto> ordersDtos = records.stream().map((item) -> {
            // 拷贝数据
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            // 获取订单号
            String number = item.getNumber();
            // 根据订单号查询
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, number);
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtos);

        return R.success(ordersDtoPage);
    }
}
