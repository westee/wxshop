package com.westee.wxshop.service;

import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;
import com.westee.api.rpc.OrderRpcService;
import com.westee.wxshop.entity.GoodsWithNumber;
import com.westee.wxshop.entity.HttpException;
import com.westee.wxshop.entity.OrderResponse;
import com.westee.wxshop.generate.Goods;
import com.westee.wxshop.generate.GoodsMapper;
import com.westee.wxshop.generate.ShopMapper;
import com.westee.wxshop.generate.UserMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    private ShopMapper shopMapper;

    @Autowired
    public OrderService(UserMapper userMapper, GoodsMapper goodsMapper, ShopMapper shopMapper, GoodsService goodsService) {
        this.userMapper = userMapper;
        this.goodsMapper = goodsMapper;
        this.goodsService = goodsService;
        this.shopMapper = shopMapper;
    }

    public void deductStock(OrderInfo orderInfo) {
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {
        Order order = new Order();
        List<Long> goodsId = orderInfo.getGoods()
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);

        orderRpcService.createOrder(orderInfo, order);
        order.setUserId(userId);
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(calculateTotalPrice(orderInfo, idToGoodsMap));

        Order createOrder = orderRpcService.createOrder(orderInfo, order);

        OrderResponse response = new OrderResponse(createOrder);
        Long shopId = new ArrayList<>(idToGoodsMap.values()).get(0).getShopId();
        response.setShop(shopMapper.selectByPrimaryKey(shopId));

        response.setGoods(
                orderInfo.getGoods()
                        .stream()
                        .map(goods -> toGoodsWithNumber(goods, idToGoodsMap))
                        .collect(Collectors.toList()));
        return response;
    }


    private GoodsWithNumber toGoodsWithNumber(GoodsInfo goodsInfo, Map<Long, Goods> idToGoodsMap) {
        GoodsWithNumber result = new GoodsWithNumber(idToGoodsMap.get(goodsInfo.getId()));
        result.setNumber(goodsInfo.getNumber());
        return result;
    }


    private BigDecimal calculateTotalPrice(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap) {

        BigDecimal result = BigDecimal.ZERO;
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            Goods goods = idToGoodsMap.get(goodsInfo.getId());
            if (goods == null) {
                throw HttpException.badRequest("id非法" + goodsInfo.getId());
            }
            if (goodsInfo.getNumber() <= 0) {
                throw HttpException.badRequest("number非法" + goodsInfo.getNumber());
            }

            result = result.add(goods.getPrice().multiply(new BigDecimal(goodsInfo.getNumber())));
        }
        return result;
    }
}
