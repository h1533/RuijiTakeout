package com.jh.reggie.service;

import com.jh.reggie.model.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author JH
 * @description 针对表【orders(订单表)】的数据库操作Service
 * @createDate 2023-01-02 09:01:24
 */
public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     *
     * @param orders
     */
    void submit(Orders orders);
}
