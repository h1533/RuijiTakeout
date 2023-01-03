package com.jh.reggie.model.dto;

import com.jh.reggie.model.entity.OrderDetail;
import com.jh.reggie.model.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
