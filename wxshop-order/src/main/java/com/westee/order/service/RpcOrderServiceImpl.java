package com.westee.order.service;


import com.westee.api.DataStatus;
import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;
import com.westee.api.generate.OrderMapper;
import com.westee.api.rpc.OrderService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.function.BooleanSupplier;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order createOrder(OrderInfo userId, Order order) {
        insertOrder(order);
        return null;
    }

    private void insertOrder(Order order) {
        order.setStatus(DataStatus.PENDING.getName());

        verify(() -> order.getUserId() == null, "userId不能为空");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法");
        verify(() -> order.getAddress() == null, "address不能为空");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }


}
