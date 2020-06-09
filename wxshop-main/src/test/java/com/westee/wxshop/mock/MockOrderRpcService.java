package com.westee.wxshop.mock;

import com.westee.api.DataStatus;
import com.westee.api.data.OrderInfo;
import com.westee.api.data.PageResponse;
import com.westee.api.data.RpcOrderGoods;
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

    @Override
    public RpcOrderGoods deleteOrder(long orderId, long userId) {
        return orderRpcService.deleteOrder(orderId, userId);
    }

    @Override
    public PageResponse<RpcOrderGoods> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        return orderRpcService.getOrder(userId, pageNum, pageSize, status);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRpcService.getOrderById(orderId);
    }

    @Override
    public RpcOrderGoods updateOrder(Order order) {
        return orderRpcService.updateOrder(order);
    }

}
