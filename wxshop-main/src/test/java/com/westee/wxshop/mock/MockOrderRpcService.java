package com.westee.wxshop.mock;

import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;
import com.westee.api.rpc.OrderRpcService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wxshop.orderservice.version}")
public class MockOrderRpcService implements OrderRpcService {
    @Mock
    public OrderRpcService orderRpcService;

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        return orderRpcService.createOrder(orderInfo, order);
    }

    @Override
    public void deductStock(OrderInfo orderInfo) {

    }

}
