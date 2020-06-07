package com.westee.wxshop.service;

import com.westee.api.DataStatus;
import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;
import com.westee.api.data.RpcOrderGoods;
import com.westee.api.generate.Order;
import com.westee.api.rpc.OrderRpcService;
import com.westee.wxshop.dao.GoodsStockMapper;
import com.westee.wxshop.entity.GoodsWithNumber;
import com.westee.api.exceptions.HttpException;
import com.westee.wxshop.entity.OrderResponse;
import com.westee.api.data.PageResponse;
import com.westee.wxshop.generate.Goods;
import com.westee.wxshop.generate.Shop;
import com.westee.wxshop.generate.ShopMapper;
import com.westee.wxshop.generate.UserMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public OrderService(UserMapper userMapper, GoodsStockMapper goodsStockMapper, ShopMapper shopMapper,
                        GoodsService goodsService, SqlSessionFactory sqlSessionFactory) {
        this.userMapper = userMapper;
        this.goodsStockMapper = goodsStockMapper;
        this.goodsService = goodsService;
        this.shopMapper = shopMapper;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Transactional
    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) throws Exception {
        // 扣减库存失败
//        if (!deductStock(orderInfo)) {
//            throw HttpException.gone("扣减库存失败");
//        }
        deductStock(orderInfo);

        Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(orderInfo.getGoods());
        Order createOrder = createdOrderViaRpc(orderInfo, idToGoodsMap, userId);

        return generateResponse(createOrder, idToGoodsMap, orderInfo.getGoods());
    }

    private OrderResponse generateResponse(Order createOrder, Map<Long, Goods> idToGoodsMap, List<GoodsInfo> goodsInfos) {
        OrderResponse response = new OrderResponse(createOrder);
        Long shopId = new ArrayList<>(idToGoodsMap.values()).get(0).getShopId();
        response.setShop(shopMapper.selectByPrimaryKey(shopId));

        response.setGoods(
                goodsInfos
                        .stream()
                        .map(goods -> toGoodsWithNumber(goods, idToGoodsMap))
                        .collect(Collectors.toList()));
        return response;

    }

    private Map<Long, Goods> getIdTOGoodsMap(List<GoodsInfo > goodsInfo) {
        List<Long> goodsId = goodsInfo
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);
        return idToGoodsMap;
    }

    private Order createdOrderViaRpc(OrderInfo orderInfo, Map<Long, Goods> idToGoodsMap, long userId) {
        Order order = new Order();

        order.setUserId(userId);
        order.setStatus(DataStatus.PENDING.getName());
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(calculateTotalPrice(orderInfo, idToGoodsMap));
        return orderRpcService.createOrder(orderInfo, order);
    }

    /**
     * 扣减库存
     *
     * @param orderInfo
     * @return 全部扣减成功，返回true，否则返回false。
     */
    @Transactional
    public void deductStock(OrderInfo orderInfo) throws Exception {
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
                LOGGER.error("扣减库存失败，商品id:" + goodsInfo.getId());
                throw HttpException.gone("扣减库存失败");
            }
        }
    }

//    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
//    public boolean deductStock(OrderInfo orderInfo) {
//        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
//            for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
//                if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
//                    LOGGER.error("扣减库存失败，商品id:" + goodsInfo.getId());
//                    sqlSession.rollback();
//                    return false;
//                }
//            }
//            sqlSession.commit();
//            return true;
//        }
//    }


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

            result = result + (goods.getPrice() * goodsInfo.getNumber());
        }
        return result;
    }

    public OrderResponse deleteOrder(long orderId, long userId) {
        RpcOrderGoods rpcOrderGoods = orderRpcService.deleteOrder(orderId, userId);
        return toOrderResponse(rpcOrderGoods);
    }

    private OrderResponse toOrderResponse(RpcOrderGoods rpcOrderGoods){
        Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(rpcOrderGoods.getGoods());
        return generateResponse(rpcOrderGoods.getOrder(), idToGoodsMap, rpcOrderGoods.getGoods());
    }

    public PageResponse<OrderResponse> getOrder(Long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        PageResponse<RpcOrderGoods> rpcOrderGoods = orderRpcService.getOrder(userId, pageNum, pageSize, status);

        List<GoodsInfo> goodsInfo = rpcOrderGoods
                .getData()
                .stream()
                .map(RpcOrderGoods::getGoods)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Map<Long, Goods> idToGoodsMap = getIdTOGoodsMap(goodsInfo);
        List<OrderResponse> orders = rpcOrderGoods.getData()
                .stream()
                .map(order -> generateResponse(order.getOrder(), idToGoodsMap, order.getGoods()))
                .collect( Collectors.toList());

        return PageResponse.pageData(
                rpcOrderGoods.getPageNum(),
                rpcOrderGoods.getPageSize(),
                rpcOrderGoods.getTotalPage(),
                orders
        );
    }

    public Object getOrderById(Long id, long id1) {
        return null;
    }

    public OrderResponse updateExpressInformation(Order order, Long userId) {
        Order orderInDatabase = orderRpcService.getOrderById(order.getId());
        if(orderInDatabase == null){
            throw HttpException.notFound("订单未找到，id="+order.getId());
        }

        Shop shop = shopMapper.selectByPrimaryKey(orderInDatabase.getShopId());
        if(shop == null){
            throw HttpException.notFound(("店铺未找到"));
        }

        if(!shop.getOwnerUserId().equals(userId)){
            throw HttpException.forbidden("禁止访问");
        }

        Order copy = new Order();
        copy.setId(order.getId());
        copy.setExpressId(order.getExpressId());
        copy.setExpressCompany(order.getExpressCompany());
        return toOrderResponse(orderRpcService.updateOrder(copy));
    }

    public Object updateOrderStatus(Order order, Long userId) {
        Order orderInDatabase = orderRpcService.getOrderById(order.getId());
        if(orderInDatabase == null){
            throw HttpException.notFound("订单未找到，id="+order.getId());
        }


    }
}
