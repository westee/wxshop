package com.westee.api.rpc;

import com.westee.api.DataStatus;
import com.westee.api.data.OrderInfo;
import com.westee.api.data.PageResponse;
import com.westee.api.data.RpcOrderGoods;
import com.westee.api.generate.Order;

public interface OrderRpcService {
    Order createOrder(OrderInfo userId, Order order);

    void deductStock(OrderInfo orderInfo);

    RpcOrderGoods deleteOrder(long orderId, long userId);

    PageResponse<RpcOrderGoods> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status);
}
