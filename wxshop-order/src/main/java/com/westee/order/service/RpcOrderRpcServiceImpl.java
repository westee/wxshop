package com.westee.order.service;

import com.westee.api.DataStatus;
import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;
import com.westee.api.data.RpcOrderGoods;
import com.westee.api.exceptions.HttpException;
import com.westee.api.generate.Order;
import com.westee.api.generate.OrderMapper;
import com.westee.api.rpc.OrderRpcService;
import com.westee.order.mapper.MyOrderMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.http.HTTPException;
import javax.xml.ws.spi.http.HttpExchange;
import java.util.Date;
import java.util.List;
import java.util.function.BooleanSupplier;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderRpcServiceImpl implements OrderRpcService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MyOrderMapper myOrderMapper;

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.insertOrders(orderInfo);
        return order;
    }

    @Override
    public void deductStock(OrderInfo orderInfo) {

    }

    @Override
    public RpcOrderGoods deleteOrder(long orderId, long userId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order==null){
            throw HttpException.notFound("订单未找到");
        }

        if (order.getUserId()!=userId) {
            throw HttpException.forbidden("无权访问");
        }

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);

        order.setStatus(DataStatus.DELETED.getName());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKey(order);

        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return result;
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

        long id = orderMapper.insert(order);
        order.setId(id);
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }


}
