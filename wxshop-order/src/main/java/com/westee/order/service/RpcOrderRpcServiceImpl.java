package com.westee.order.service;

import com.westee.api.DataStatus;
import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;
import com.westee.api.data.PageResponse;
import com.westee.api.data.RpcOrderGoods;
import com.westee.api.exceptions.HttpException;
import com.westee.api.generate.*;
import com.westee.api.rpc.OrderRpcService;
import com.westee.order.mapper.MyOrderMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderRpcServiceImpl implements OrderRpcService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MyOrderMapper myOrderMapper;

    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

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
        if (order == null) {
            throw HttpException.notFound("订单未找到");
        }

        if (order.getUserId() != userId) {
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

    @Override
    public PageResponse<RpcOrderGoods> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        OrderExample countByStatus = new OrderExample();
        setStatus(status, countByStatus);

        int count = (int)orderMapper.countByExample(countByStatus);
        OrderExample pagedOrder = new OrderExample();
        pagedOrder.setOffset((pageNum - 1) * pageSize);
        pagedOrder.setLimit(pageNum);
        setStatus(status, pagedOrder)
                .andUserIdEqualTo(userId);
        List<Order> orders = orderMapper.selectByExample(pagedOrder);
        List<Long> orderIds = orders.stream().map(Order::getId).collect(toList());
        OrderGoodsExample selectByOrderIds = new OrderGoodsExample();
        selectByOrderIds.createCriteria().andOrderIdIn(orderIds);
        List<OrderGoods> orderGoods = orderGoodsMapper.selectByExample(selectByOrderIds);

        int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        Map<Long, List<OrderGoods>> orderIdToGoodsMap = orderGoods
                .stream()
                .collect(Collectors.groupingBy(OrderGoods::getOrderId, Collectors.toList()));

        List<RpcOrderGoods> rpcOrderGoods = orders.stream()
                .map(order -> toRpcOrderGoods(order, orderIdToGoodsMap))
                .collect(toList());

        return PageResponse.pageData(pageNum, pageSize,totalPage, rpcOrderGoods);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    @Override
    public RpcOrderGoods updateOrder(Order order) {
        orderMapper.updateByPrimaryKey(order);

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(order.getId());

        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(orderMapper.selectByPrimaryKey(order.getId()));
        return result;
    }

    private RpcOrderGoods toRpcOrderGoods(Order order,  Map<Long, List<OrderGoods>> orderIdToGoodsMap   ){
        RpcOrderGoods result = new RpcOrderGoods();
        result.setOrder(order);
        List<GoodsInfo> goodsInfos = orderIdToGoodsMap.getOrDefault(order.getId(), Collections.emptyList())
                .stream()
                .map(this::toGoodsInfo)
                .collect(toList());

        result.setGoods(goodsInfos);
        return result;
    }

    private GoodsInfo toGoodsInfo(OrderGoods orderGoods){
        GoodsInfo result = new GoodsInfo();
        result.setId(orderGoods.getGoodsId());
        result.setNumber(orderGoods.getNumber().intValue());
        return  result;
    }

    private OrderExample.Criteria setStatus(DataStatus status, OrderExample countByStatus) {
        if (status == null) {
            return countByStatus.createCriteria().andStatusNotEqualTo(DataStatus.DELETED.getName());
        } else {
            return countByStatus.createCriteria().andStatusNotEqualTo(DataStatus.DELETED.getName());
        }

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
