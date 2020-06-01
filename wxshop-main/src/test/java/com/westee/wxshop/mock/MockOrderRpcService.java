package com.westee.wxshop.mock;

import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;
import com.westee.api.rpc.OrderRpcService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wxshop.orderservice.version}")
public class MockOrderRpcService implements OrderRpcService {
    @Mock
    OrderRpcService orderRpcService;

    @Override
    public Order createOrder(OrderInfo userId, Order order) {
        return null;
    }

    @Override
    public void deductStock(OrderInfo orderInfo) {

    }

}
