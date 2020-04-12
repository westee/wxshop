package com.westee.wxshop.dao;

import com.westee.wxshop.generate.Shop;
import com.westee.wxshop.generate.ShopMapper;
import com.westee.wxshop.generate.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopDao {
    private final ShopMapper shopMapper;

    @Autowired
    public ShopDao(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }


    public Shop findShopById(Long shopId) {
        return shopMapper.selectByPrimaryKey(shopId);
    }
}