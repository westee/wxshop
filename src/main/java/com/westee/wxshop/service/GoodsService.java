package com.westee.wxshop.service;

import com.westee.wxshop.dao.GoodsDao;
import com.westee.wxshop.dao.ShopDao;
import com.westee.wxshop.generate.Goods;
import com.westee.wxshop.generate.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class GoodsService {
    private GoodsDao goodsDao;
    private ShopDao shopDao;

    // 不注入在创建商品时会出现空指针异常
    @Autowired
    public GoodsService(GoodsDao goodsDao, ShopDao shopDao) {
        this.goodsDao = goodsDao;
        this.shopDao = shopDao;
    }


    public Goods createGoods(Goods goods) {
//        Shop shop = shopDao.findShopById(goods.getShopId());

//        if(Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())){
            return goodsDao.insertGoods(goods);
//        }
//        throw new NotAuthorized("无权访问");
    }

    public Goods deleteGoodsById(Long goodsId) {
        Shop shop = shopDao.findShopById(goodsId);

        if(Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())){
            return goodsDao.deleteGoodsById(goodsId);
        }
        throw new NotAuthorized("无权访问");
    }

    public static class NotAuthorized extends RuntimeException{
        public NotAuthorized(String message) {
            super(message);
        }
    }

}