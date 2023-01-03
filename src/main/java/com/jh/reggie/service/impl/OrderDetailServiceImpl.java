package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.model.entity.OrderDetail;
import com.jh.reggie.service.OrderDetailService;
import com.jh.reggie.mappers.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author JH
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-01-02 09:01:29
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




