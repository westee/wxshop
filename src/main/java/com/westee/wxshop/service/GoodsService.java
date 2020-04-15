package com.westee.wxshop.service;

import com.westee.wxshop.entity.DataStatus;
import com.westee.wxshop.entity.PageResponse;
import com.westee.wxshop.generate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class GoodsService {
    private GoodsMapper goodsMapper;
    private ShopMapper shopMapper;

    // 不注入在创建商品时会出现空指针异常
    @Autowired
    public GoodsService(GoodsMapper goodsMapper, ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    public Goods createGoods(Goods goods) {
        Shop shop = shopMapper.selectByPrimaryKey(goods.getId());

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            long id = goodsMapper.insert(goods);
            goods.setId(id);
            ;
            return goods;
        }
        throw new NotAuthorized("无权访问");
    }

    public Goods deleteGoodsById(Long goodsId) {
        Shop shop = shopMapper.selectByPrimaryKey(goodsId);

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);

            if (goods == null) {
                throw new ResourceNotFoundException("商品未找到");
            }

            goods.setStatus(DataStatus.DELETED.getName());
            goodsMapper.updateByPrimaryKey(goods);
        }
        throw new NotAuthorized("无权访问");
    }

    public PageResponse<Goods> getGoods(Integer pageNum, Integer pageSize, Integer shopId) {
        // 1. 元素总个数
        // 2. 计算总页数
        // 3. 正确的分页
        long totalNumber = countGoods(shopId);

    }

    public int countGoods(Integer shopId) {
        if (shopId == null) {
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.createCriteria()
                    .andStatusEqualTo(DataStatus.OK.getName())
                    .andShopIdEqualTo(shopId.longValue());
            return (int) goodsMapper.countByExample(goodsExample);
        } else {
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.createCriteria().andStatusEqualTo(DataStatus.OK.getName());
            return (int) goodsMapper.countByExample(goodsExample);
        }
    }

    public static class NotAuthorized extends RuntimeException {
        public NotAuthorized(String message) {
            super(message);
        }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}