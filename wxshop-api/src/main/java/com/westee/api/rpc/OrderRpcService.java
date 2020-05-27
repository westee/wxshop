package com.westee.api.rpc;

import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;

public interface OrderRpcService {
     Order createOrder(OrderInfo userId, Order order);

    void deductStock(OrderInfo orderInfo);
}
