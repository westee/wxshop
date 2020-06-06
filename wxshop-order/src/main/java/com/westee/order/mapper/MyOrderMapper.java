package com.westee.order.mapper;

import com.westee.api.data.GoodsInfo;
import com.westee.api.data.OrderInfo;

import java.util.List;


public interface MyOrderMapper {
    void insertOrders(OrderInfo orderInfo);
    List<GoodsInfo> getGoodsInfoOfOrder (long userId);
}
