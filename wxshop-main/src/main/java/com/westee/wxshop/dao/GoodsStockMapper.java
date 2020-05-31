package com.westee.wxshop.dao;

import com.westee.api.data.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsStockMapper {

    int deductStock(GoodsInfo goodsInfo);
}
