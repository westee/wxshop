package com.westee.wxshop.service;

import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;
import com.westee.api.rpc.OrderRpcService;
import com.westee.wxshop.entity.OrderResponse;
import com.westee.wxshop.generate.Goods;
import com.westee.wxshop.generate.GoodsExample;
import com.westee.wxshop.generate.GoodsMapper;
import com.westee.wxshop.generate.UserMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Reference(version = "${demo.service.version}")
    private OrderRpcService orderRpcService;

    private UserMapper userMapper;

    private GoodsMapper goodsMapper;

    private GoodsService goodsService;

    @Autowired
    public OrderService(UserMapper userMapper, GoodsMapper goodsMapper, GoodsService goodsService) {
        this.userMapper = userMapper;
        this.goodsMapper = goodsMapper;
        this.goodsService = goodsService;
    }

    public void deductStock(OrderInfo orderInfo) {
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {
        Order order = new Order();
        orderRpcService.createOrder(orderInfo, order);
        order.setUserId(userId);
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(calculateTotalPrice(orderInfo));
        return null;
    }

    private BigDecimal calculateTotalPrice(OrderInfo orderInfo) {
        List<Long> goodsId = orderInfo.getGoods()
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);

        return idToGoodsMap;
    }
}
