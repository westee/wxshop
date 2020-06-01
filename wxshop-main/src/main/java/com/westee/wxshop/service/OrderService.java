package com.westee.wxshop.service;

import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;
import com.westee.api.generate.Order;
import com.westee.api.rpc.OrderRpcService;
import com.westee.wxshop.dao.GoodsStockMapper;
import com.westee.wxshop.entity.GoodsWithNumber;
import com.westee.wxshop.entity.HttpException;
import com.westee.wxshop.entity.OrderResponse;
import com.westee.wxshop.generate.Goods;
import com.westee.wxshop.generate.ShopMapper;
import com.westee.wxshop.generate.UserMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    @Reference(version = "${demo.service.version}")
    private OrderRpcService orderRpcService;

    private UserMapper userMapper;

    private GoodsStockMapper goodsStockMapper;

    private GoodsService goodsService;

    private ShopMapper shopMapper;

    private SqlSessionFactory sessionFactory;

    @Autowired
    public OrderService(UserMapper userMapper, GoodsStockMapper goodsStockMapper, ShopMapper shopMapper, GoodsService goodsService) {
        this.userMapper = userMapper;
        this.goodsStockMapper = goodsStockMapper;
        this.goodsService = goodsService;
        this.shopMapper = shopMapper;
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) throws Exception {
        // 扣减库存失败
        if (!deductStock(orderInfo)) {
            throw HttpException.gone("扣减库存失败");
        }

        Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(orderInfo);
        Order createOrder = createdOrderViaRpc(orderInfo, idToGoodsMap, userId);

        return generateResponse(createOrder, idToGoodsMap, orderInfo);
    }

    private OrderResponse generateResponse(Order createOrder, Map<Long, Goods> idToGoodsMap, OrderInfo orderInfo) {
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

    private Map<Long, Goods> getIdTOGoodsMap(OrderInfo orderInfo) {
        List<Long> goodsId = orderInfo.getGoods()
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);
        return idToGoodsMap;
    }

    private Order createdOrderViaRpc(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap, long userId) {
        Order order = new Order();

        order.setUserId(userId);
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(calculateTotalPrice(orderInfo, idToGoodsMap));
        Order createOrder = orderRpcService.createOrder(orderInfo, order);
        return createOrder;
    }

    /**
     * 扣减库存
     *
     * @param orderInfo
     * @return 全部扣减成功，返回true，否则返回false。
     */
    // RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE
    public boolean deductStock(OrderInfo orderInfo) {
        try (SqlSession sqlSession = sessionFactory.openSession(false)) {
            for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
                if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
                    LOGGER.error("扣减库存失败，商品id:" + goodsInfo.getId());
                    sqlSession.rollback();
                    return false;
                }
            }
            sqlSession.commit();
            return true;
        }
    }


    private GoodsWithNumber toGoodsWithNumber(GoodsInfo goodsInfo, Map<Long, Goods> idToGoodsMap) {
        GoodsWithNumber result = new GoodsWithNumber(idToGoodsMap.get(goodsInfo.getId()));
        result.setNumber(goodsInfo.getNumber());
        return result;
    }


    private long calculateTotalPrice(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap) {
        long result = 0;
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            Goods goods = idToGoodsMap.get(goodsInfo.getId());
            if (goods == null) {
                throw HttpException.badRequest("id非法" + goodsInfo.getId());
            }
            if (goodsInfo.getNumber() <= 0) {
                throw HttpException.badRequest("number非法" + goodsInfo.getNumber());
            }

            result = result + (goods.getPrice()*goodsInfo.getNumber());
        }
        return result;
    }
}
